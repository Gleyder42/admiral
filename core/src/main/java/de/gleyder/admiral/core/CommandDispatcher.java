package de.gleyder.admiral.core;

import de.gleyder.admiral.core.error.AmbiguousCommandError;
import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.error.LiteralCommandError;
import de.gleyder.admiral.core.error.ThrowableCommandError;
import de.gleyder.admiral.core.executor.CheckResult;
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
  }

  public List<CommandError> dispatch(@NonNull String command, @NonNull Object source,
                                     @NonNull Map<String, Object> interpreterMap) {
    return dispatch(findRoute(command, interpreterMap), source);
  }

  List<CommandError> dispatch(@NonNull CommandRoute route, @NonNull Object source) {
    if (route.isInvalid()) {
      return route.getErrors();
    }
    CommandContext context = new CommandContext(source, route.getValueBag());

    List<CommandError> commandErrors = new ArrayList<>();
    for (CommandNode node : route.getNodeList()) {
      if (node.getCheck().isPresent()) {
        try {
          CheckResult checkResult = node.getCheck().get().test(context);
          if (checkResult.getError().isPresent()) {
            commandErrors.add(checkResult.getError().get());
            break;
          }
        } catch (Exception exception) {
          commandErrors.add(new ThrowableCommandError(exception));
          break;
        }
      }
    }

    if (!commandErrors.isEmpty()) {
      return commandErrors;
    }

    route.getNodeList().stream()
        .filter(node -> node.getExecutor().isPresent())
        .forEach(node -> node.getExecutor().get().execute(context));

    return Collections.emptyList();
  }

  public List<CommandRoute> getRoutes(@NonNull CommandNode node) {
    return getAllRoutes(node);
  }

  public List<CommandRoute> getAllRoutes() {
    return getAllRoutes(rootNode);
  }

  private List<CommandRoute> getAllRoutes(@NonNull CommandNode node) {
    List<CommandRoute> routeList = new ArrayList<>();
    findAllRoutes(routeList, new CommandRoute(), node);
    return routeList;
  }

  private void findAllRoutes(@NonNull List<CommandRoute> routeList, @NonNull CommandRoute route, @NonNull CommandNode node) {
    route.add(node);
    node.getAllNodes().forEach(nextNode -> findAllRoutes(routeList, route.duplicate(), nextNode));

    if (node.isLeaf() || (!node.isLeaf() && route.hasExecutor())) {
      routeList.add(route);
    }
  }

  public CommandRoute findRoute(@NonNull Deque<InputArgument> argumentDeque, @NonNull Map<String, Object> interpreterMap) {
    CommandRoute commandRoute = new CommandRoute();
    route(rootNode, commandRoute, argumentDeque, interpreterMap);
    return commandRoute;
  }

  public CommandRoute findRoute(@NonNull String command, @NonNull Map<String, Object> interpreterMap) {
    return findRoute(new ArrayDeque<>(parser.parse(command)), interpreterMap);
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
        Optional<List<CommandError>> reduce = commandRouteList.stream()
            .filter(CommandRoute::hasErrors)
            .map(CommandRoute::getErrors)
            .reduce((leftErrorList, rightErrorList) -> {
              rightErrorList.addAll(leftErrorList);
              return rightErrorList;
            });

        reduce.ifPresentOrElse(
            route::addErrors,
            () -> route.addError(LiteralCommandError
                .create()
                .setMessage(Messages.NO_COMMAND_FOUND.get())
            )
        );
      } else {
        route.addError(new AmbiguousCommandError(alternateRoutes));
      }
    }
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