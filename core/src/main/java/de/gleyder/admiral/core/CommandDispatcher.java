package de.gleyder.admiral.core;

import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import de.gleyder.admiral.core.parser.InputArgument;
import de.gleyder.admiral.core.parser.InputParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class CommandDispatcher {

  @Getter
  private final StaticNode rootNode = new StaticNode("root");
  private final InputParser parser;

  @Setter
  private Consumer<CommandContext> afterExecute;

  public CommandDispatcher(@Nullable InputParser parser) {
    this.parser = AdmiralCommon.standard(parser, new InputParser());
  }

  public CommandDispatcher() {
    this(null);
  }

  public void registerCommand(@NonNull StaticNode node) {
    rootNode.addNode(node);
  }

  public List<CommandError> dispatch(@NonNull String command, Object source, @NonNull Map<String, Object> interpreterMap) {
    CommandRoute commandRoute = new CommandRoute();
    List<InputArgument> argumentList = parser.parse(command);
    route(rootNode, commandRoute, new ArrayDeque<>(argumentList), interpreterMap);

    if (commandRoute.isInvalid()) {
      commandRoute.addError(LiteralCommandError.create().setMessage("Command is invalid"));
      return commandRoute.getErrors();
    }

    ValueBag valueBag = new ValueBag();
    CommandContext context = new CommandContext(source, valueBag);

    int index = 1;
    for (InputArgument argument : argumentList) {
      CommandNode node = commandRoute.get(index);
      node.onCommandProcess(context, interpreterMap, argument);
      index++;
    }


    List<CommandError> commandErrors = commandRoute.getNodeList().stream()
            .filter(node -> node.getCheck().isPresent())
            .map(node -> node.getCheck().get().test(context))
            .filter(checkResult -> !checkResult.wasSuccessful())
            .map(checkResult -> checkResult.getError().orElseThrow())
            .collect(Collectors.toUnmodifiableList());

    if (!commandErrors.isEmpty()) {
      return commandErrors;
    }

    commandRoute.getNodeList().stream()
            .filter(node -> node.getExecutor().isPresent())
            .forEach(node -> node.getExecutor().get().execute(context));

    if (afterExecute != null) {
      afterExecute.accept(context);
    }

    return Collections.emptyList();
  }

  public List<CommandRoute> getAllRoutes() {
    return getAllRoutes(rootNode);
  }

  public List<CommandRoute> getRoutes(@NonNull CommandNode node) {
    return getAllRoutes(node);
  }

  private List<CommandRoute> getAllRoutes(@NonNull CommandNode node) {
    List<CommandRoute> routeList = new ArrayList<>();
    findAllRoutes(routeList, new CommandRoute(), node);
    return routeList;
  }

  private void findAllRoutes(@NonNull List<CommandRoute> routeList, @NonNull CommandRoute route, @NonNull CommandNode node) {
    route.add(node);
    node.getAllNodes().forEach(nextNode -> findAllRoutes(routeList, route.duplicate(), nextNode));

    if (node.isLeaf() || (!node.isLeaf() && node.getExecutor().isPresent())) {
      routeList.add(route);
    }
  }

  @TestOnly
  CommandRoute findRoute(@NonNull Deque<InputArgument> argumentDeque) {
    CommandRoute commandRoute = new CommandRoute();
    route(rootNode, commandRoute, argumentDeque, new HashMap<>());
    return commandRoute;
  }

  public CommandRoute findRoute(@NonNull String command) {
    return findRoute(new ArrayDeque<>(parser.parse(command)));
  }

  private void route(@NonNull CommandNode node, @NonNull CommandRoute route,
                     @NonNull Deque<InputArgument> argumentDeque, @NonNull Map<String, Object> interpreterMap) {
    route.add(node);
    if (node.isLeaf()) {
      if (!argumentDeque.isEmpty()) {
        route.addError(LiteralCommandError.create()
                .setDetailed("Further arguments remain [%s] but command tree has finished with node %s",
                        argumentDeque, node.getKey()
                )
                .setSimple("No command found")
        );
      }
      return;
    }

    if (argumentDeque.isEmpty()) {
      if (node.getExecutor().isEmpty()) {
        route.addError(LiteralCommandError.create()
                .setDetailed("No executor was found for node %s", node.getKey())
                .setSimple("No command found")
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

    List<DynamicNode> dynamicNodeList = getDynamicNodes(node, route, interpreterMap, inputArgument);
    if (dynamicNodeList.size() == 1) {
      route(dynamicNodeList.get(0), route, argumentDeque, interpreterMap);
    } else {
      List<CommandRoute> alternateRoutes = getAlternateRoutes(route, argumentDeque, interpreterMap, dynamicNodeList);

      route.clearNodes();
      if (alternateRoutes.size() == 1) {
        route.addAll(alternateRoutes.get(0));
      } else if (alternateRoutes.size() > 1) {
        route.addError(new AmbiguousCommandError(alternateRoutes));
      }
    }
  }

  @NotNull
  private List<DynamicNode> getDynamicNodes(@NonNull CommandNode node, @NonNull CommandRoute route,
                                            @NonNull Map<String, Object> interpreterMap,
                                            @NonNull InputArgument inputArgument) {
    return node.getDynamicNodes().stream()
            .filter(filterNode -> {
              List<InterpreterResult<Object>> interpreterResults =
                      filterNode.getInterpreterStrategy().test(interpreterMap, filterNode.getInterpreter(), inputArgument);
              interpreterResults.stream()
                      .filter(InterpreterResult::failed)
                      .forEach(result -> route.addError(result.getError().orElseThrow()));
              return interpreterResults.stream().noneMatch(InterpreterResult::failed);
            })
            .collect(Collectors.toUnmodifiableList());
  }

  @NotNull
  private List<CommandRoute> getAlternateRoutes(@NonNull CommandRoute route, @NonNull Deque<InputArgument> argumentDeque,
                                                @NonNull Map<String, Object> interpreterMap,
                                                @NonNull List<DynamicNode> dynamicNodeList) {
    return dynamicNodeList.stream()
            .map(undeterminedNode -> {
              CommandRoute copyRoute = route.duplicate();
              route(undeterminedNode, copyRoute, new ArrayDeque<>(argumentDeque), interpreterMap);
              return copyRoute;
            })
            .filter(CommandRoute::isValid)
            .collect(Collectors.toUnmodifiableList());
  }

}