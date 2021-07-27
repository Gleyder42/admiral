package de.gleyder.admiral.core;

import de.gleyder.admiral.core.error.AmbiguousCommandError;
import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.error.LiteralCommandError;
import de.gleyder.admiral.core.error.ThrowableCommandError;
import de.gleyder.admiral.core.executor.Check;
import de.gleyder.admiral.core.executor.CheckResult;
import de.gleyder.admiral.core.help.HelpCommand;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import de.gleyder.admiral.core.parser.InputArgument;
import de.gleyder.admiral.core.parser.InputParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Admirals main class.
 * Used for command execution and registration
 */
@Slf4j
public class CommandDispatcher {

  @TestOnly
  @Getter(AccessLevel.PACKAGE)
  private final StaticNode rootNode = new StaticNode("root");

  private final InputParser parser;

  public CommandDispatcher(@Nullable InputParser parser) {
    this.parser = Objects.requireNonNullElse(parser, new InputParser());
  }

  public CommandDispatcher() {
    this(null);
  }

  public void registerCommand(@NonNull StaticNode node) {
    rootNode.addNode(node);
    new HelpCommand(node);
  }

  public List<CommandError> dispatch(@NonNull String command, @NonNull CommandSource source,
                                     @NonNull Map<String, Object> interpreterMap) {
    return dispatch(findRoute(command, interpreterMap), source);
  }

  List<CommandError> dispatch(@NonNull CommandRoute route, @NonNull CommandSource source) {
    if (route.isInvalid()) {
      return route.getErrors();
    }
    CommandContext context = new CommandContext(source, route.getValueBag());

    List<CommandError> commandErrors = new ArrayList<>();
    route.getNodeList().stream()
        .map(node -> node.getCheck().map(check -> testCheck(context, check)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .ifPresent(commandErrors::add);

    if (!commandErrors.isEmpty()) {
      return commandErrors;
    }

    route.getNodeList().stream()
        .filter(node -> node.getExecutor().isPresent())
        .forEach(node -> node.getExecutor().get().execute(context));

    return Collections.emptyList();
  }

  public CommandRoute findRoute(@NonNull Deque<InputArgument> argumentDeque, @NonNull Map<String, Object> interpreterMap) {
    CommandRoute commandRoute = new CommandRoute();
    route(rootNode, commandRoute, argumentDeque, interpreterMap);
    return commandRoute;
  }

  public CommandRoute findRoute(@NonNull String command, @NonNull Map<String, Object> interpreterMap) {
    return findRoute(new ArrayDeque<>(parser.parse(command)), interpreterMap);
  }

  public List<CommandRoute> getAllRoutes() {
    return rootNode.getAllRoutes();
  }

  private CommandError testCheck(CommandContext context, Check check) {
    try {
      CheckResult checkResult = check.test(context);
      Optional<CommandError> errorOptional = checkResult.getError();
      return errorOptional.orElse(null);
    } catch (Exception exception) {
      return new ThrowableCommandError(exception);
    }
  }

  private void route(@NonNull CommandNode node, @NonNull CommandRoute route,
                     @NonNull Deque<InputArgument> argumentDeque, @NonNull Map<String, Object> interpreterMap) {
    route.add(node);
    if (node.isLeaf()) {
      if (!argumentDeque.isEmpty()) {
        route.addError(LiteralCommandError.create()
            .setDetailed(Messages.FURTHER_ARGUMENTS_REMAIN.get(argumentDeque, node.getKey()))
            .setSimple(Messages.NO_COMMAND_FOUND.get())
        );
      }
      return;
    }

    if (argumentDeque.isEmpty()) {
      if (!route.hasExecutor()) {
        route.addError(LiteralCommandError.create()
            .setDetailed(Messages.NO_EXECUTOR_ON_ROUTE.get())
            .setSimple(Messages.NO_COMMAND_FOUND.get())
        );
      }
      return;
    }

    InputArgument inputArgument = argumentDeque.pop();
    Optional<CommandNode> nextNodeOptional = node.getNextNode(inputArgument.getMerged());

    if (inputArgument.isSingle() && nextNodeOptional.isPresent()) {
      route(nextNodeOptional.get(), route, argumentDeque, interpreterMap);
      return;
    }

    List<CommandRoute> commandRouteList = getDynamicNodes(node, interpreterMap, inputArgument);
    List<CommandRoute> succeedRoutes = commandRouteList.stream()
        .filter(Predicate.not(CommandRoute::hasErrors))
        .collect(Collectors.toUnmodifiableList());

    if (succeedRoutes.size() == 1) {
      CommandRoute nextRoute = succeedRoutes.get(0);
      route.getValueBag().addBag(nextRoute.getValueBag());

      route(nextRoute.getNodeList().get(0), route, argumentDeque, interpreterMap);
    } else {
      List<CommandRoute> alternateRoutes = getAlternateRoutes(route, succeedRoutes, argumentDeque, interpreterMap);

      route.clearNodes();
      if (alternateRoutes.size() == 1) {
        route.addAll(alternateRoutes.get(0));
      } else if (alternateRoutes.isEmpty()) {
        addErrors(route, commandRouteList);
      } else {
        route.addError(new AmbiguousCommandError(alternateRoutes));
      }
    }
  }

  private void addErrors(@NotNull CommandRoute route, List<CommandRoute> commandRouteList) {
    commandRouteList.stream()
        .filter(CommandRoute::hasErrors)
        .map(CommandRoute::getErrors)
        .reduce((leftErrorList, rightErrorList) -> {
          rightErrorList.addAll(leftErrorList);
          return rightErrorList;
        })
        .ifPresentOrElse(
            route::addErrors,
            () -> route.addError(LiteralCommandError
                .create()
                .setMessage(Messages.NO_COMMAND_FOUND.get())
            )
        );
  }

  private List<CommandRoute> getDynamicNodes(@NonNull CommandNode node, @NonNull Map<String, Object> interpreterMap,
                                             @NonNull InputArgument argument) {
    return node.getDynamicNodes().stream()
        .map(nextNode -> {
          CommandRoute duplicate = new CommandRoute();
          parseInterpreter(nextNode, duplicate, interpreterMap, argument);
          return duplicate;
        })
        .collect(Collectors.toUnmodifiableList());
  }

  private void parseInterpreter(@NonNull DynamicNode node, @NonNull CommandRoute route,
                                @NonNull Map<String, Object> interpreterMap, @NonNull InputArgument argument) {
    route.add(node);
    List<InterpreterResult<Object>> interpreterResults =
        node.getInterpreterStrategy().test(interpreterMap, node.getInterpreter(), argument);

    interpreterResults.stream()
        .filter(InterpreterResult::failed)
        .forEach(result -> route.addError(result.getError().orElseThrow()));

    interpreterResults.stream()
        .filter(InterpreterResult::succeeded)
        .forEach(result -> route.getValueBag().add(node.getKey(), result.getValue().orElseThrow()));
  }

  private List<CommandRoute> getAlternateRoutes(@NonNull CommandRoute mainRoute, @NonNull List<CommandRoute> routeList,
                                                @NonNull Deque<InputArgument> argumentDeque,
                                                @NonNull Map<String, Object> interpreterMap) {
    return routeList.stream()
        .map(route -> {
          CommandRoute duplicate = mainRoute.duplicate();
          duplicate.getValueBag().addBag(route.getValueBag());
          route(route.getNodeList().get(0), duplicate, new ArrayDeque<>(argumentDeque), interpreterMap);
          return duplicate;
        }).collect(Collectors.toUnmodifiableList());
  }
}