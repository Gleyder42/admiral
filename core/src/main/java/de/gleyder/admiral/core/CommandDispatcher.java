package de.gleyder.admiral.core;

import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.key.NodeKey;
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
import java.util.stream.Collectors;

@Slf4j
public class CommandDispatcher {

  @Getter(value = AccessLevel.PUBLIC)
  private final StaticNode rootNode = new StaticNode("root");
  private final InputParser parser;

  public CommandDispatcher(@Nullable InputParser parser) {
    this.parser = AdmiralCommon.standard(parser, new InputParser());
  }

  public CommandDispatcher() {
    this(null);
  }

  public void registerCommand(@NonNull StaticNode node) {
    rootNode.addNode(node);
  }

  public void dispatch(@NonNull String command, @NonNull Object source, @NonNull Map<String, Object> interpreterMap) {
    CommandRoute commandRoute = new CommandRoute();
    ArrayDeque<InputArgument> argumentDeque = new ArrayDeque<>(parser.parse(command));

    try {
      route(rootNode, commandRoute, new ArrayDeque<>(argumentDeque), interpreterMap);
    } catch (CommandDispatcherException exception) {
      log.error("Ambiguous routes found: {}", exception.getRouteList());
      return;
    }

    if (commandRoute.isInvalid()) {
      log.error("No route found");
      commandRoute.getErrors().forEach(Throwable::printStackTrace);
      return;
    }

    log.debug("Route found '{}'", commandRoute);

    ValueBag valueBag = new ValueBag();
    CommandContext<Object> context = new CommandContext<>(source, valueBag);
    int index = 1;
    while (!argumentDeque.isEmpty()) {
      InputArgument argument = argumentDeque.pop();
      CommandNode<?> node = commandRoute.get(index);

      node.onCommandProcess(context, interpreterMap, argument);

      if (node.getRequired().isPresent() && !node.getRequired().get().test(context)) {
        throw new CommandDispatcherException("Node's '" + node.getKey() + "' required test failed", List.of(commandRoute));
      }

      node.getExecutor().ifPresent(executor -> executor.execute(context));
      index++;
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

  private void route(@NonNull CommandNode<?> node, @NonNull CommandRoute route,
                     @NonNull Deque<InputArgument> argumentDeque, @NonNull Map<String, Object> interpreterMap) {
    route.add(node);
    if (node.isLeaf()) {
      if (!argumentDeque.isEmpty()) {
        route.clear();
      }
      return;
    }

    if (argumentDeque.isEmpty()) {
      if (node.getExecutor().isEmpty()) {
        route.clear();
      }
      return;
    }

    InputArgument inputArgument = argumentDeque.pop();
    if (inputArgument.isSingle()) {
      Optional<CommandNode<NodeKey>> nextNodeOptional = node.getNextNode(inputArgument.getMerged());
      if (nextNodeOptional.isPresent()) {
        route(nextNodeOptional.get(), route, argumentDeque, interpreterMap);
        return;
      }
    }

    List<DynamicNode> dynamicNodeList = node.getDynamicNodes().stream()
        .filter(filterNode -> {
          List<InterpreterResult<Object>> interpreterResults =
              filterNode.getInterpreterStrategy().test(interpreterMap, filterNode.getInterpreter(), inputArgument);
          interpreterResults.stream()
              .filter(InterpreterResult::failed)
              .forEach(result -> route.addError(result.getError().orElseThrow()));
          return interpreterResults.stream().noneMatch(InterpreterResult::failed);
        })
        .collect(Collectors.toUnmodifiableList());

    if (dynamicNodeList.size() == 1) {
      route(dynamicNodeList.get(0), route, argumentDeque, interpreterMap);
    } else {
      List<CommandRoute> alternateRoutes = dynamicNodeList.stream()
          .map(undeterminedNode -> {
            CommandRoute copyRoute = route.duplicate();
            route(undeterminedNode, copyRoute, new ArrayDeque<>(argumentDeque), interpreterMap);
            return copyRoute;
          })
          .filter(CommandRoute::isValid)
          .collect(Collectors.toUnmodifiableList());

      route.clear();
      if (alternateRoutes.size() == 1) {
        route.addAll(alternateRoutes.get(0));
      } else if (alternateRoutes.size() > 1) {
        throw new CommandDispatcherException("Multiple command routes found", alternateRoutes);
      }
    }
  }
}
