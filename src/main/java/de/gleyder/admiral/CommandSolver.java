package de.gleyder.admiral;

import de.gleyder.admiral.interpreter.InterpreterResult;
import de.gleyder.admiral.node.CommandNode;
import de.gleyder.admiral.node.NodeKey;
import de.gleyder.admiral.node.NodeKeyType;
import de.gleyder.admiral.parser.InputArgument;
import de.gleyder.admiral.parser.InputParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CommandSolver {

  @Getter(AccessLevel.PACKAGE)
  private final CommandNode rootNode = new CommandNode(NodeKey.ofStatic("root"));
  private final InputParser parser;

  public CommandSolver(@Nullable InputParser parser) {
    this.parser = AdmiralCommon.standard(parser, new InputParser());
  }

  public CommandSolver() {
    this(null);
  }

  public void registerCommand(@NonNull CommandNode node) {
    rootNode.addNode(node);
  }

  public void resolve(@NonNull String command, @NonNull Object source) {
    CommandRoute commandRoute = new CommandRoute();
    ArrayDeque<InputArgument> argumentDeque = new ArrayDeque<>(parser.parseCommand(command.split("\\s+")));

    try {
      route(rootNode, commandRoute, new ArrayDeque<>(argumentDeque));
    } catch (CommandSolverException exception) {
      log.error("Ambiguous routes found: {}", exception.getRouteList());
      return;
    }

    if (commandRoute.isInvalid()) {
      log.error("No route found!");
      commandRoute.getErrors().forEach(Throwable::printStackTrace);
      return;
    }

    log.info("Route found!: {}", commandRoute);

    ValueBag valueBag = new ValueBag();
    CommandContext<Object> context = new CommandContext<>(source, valueBag);
    int index = 1;
    while (!argumentDeque.isEmpty()) {
      InputArgument argument = argumentDeque.pop();
      CommandNode node = commandRoute.get(index);

      if (node.getRequired().isPresent() && !node.getRequired().get().test(context)) {
        continue;
      }

      List<InterpreterResult<Object>> results = node.getInterpreterStrategy().test(node.getInterpreter(), argument);
      results.forEach(result -> {
        if (result.succeeded()) {
          context.getBag().add(node.getKey().get(), result.getValue().orElseThrow());
        } else {
          result.getError().ifPresent(Throwable::printStackTrace);
        }
      });

      node.getExecutor().ifPresent(executor -> executor.execute(context));
      index++;
    }
  }

  public CommandRoute findRoute(@NonNull Deque<InputArgument> argumentDeque) {
    CommandRoute commandRoute = new CommandRoute();
    route(rootNode, commandRoute, argumentDeque);
    return commandRoute;
  }

  public CommandRoute findRoute(@NonNull String command) {
    return findRoute(new ArrayDeque<>(parser.parseCommand(command.split("\\s+"))));
  }

  private void route(@NonNull CommandNode node, @NonNull CommandRoute route,
                     @NonNull Deque<InputArgument> argumentDeque) {
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
      Optional<CommandNode> nextNodeOptional = node.getNextNode(inputArgument.getMerged());
      if (nextNodeOptional.isPresent()) {
        route(nextNodeOptional.get(), route, argumentDeque);
        return;
      }
    }

    List<CommandNode> undeterminedNodes = node.getNodes(NodeKeyType.UNDETERMINED).stream()
        .filter(filterNode -> {
          List<InterpreterResult<Object>> interpreterResults =
              filterNode.getInterpreterStrategy().test(filterNode.getInterpreter(), inputArgument);
          interpreterResults.stream()
              .filter(InterpreterResult::failed)
              .forEach(result -> route.addError(result.getError().orElseThrow()));
          return interpreterResults.stream().noneMatch(InterpreterResult::failed);
        })
        .collect(Collectors.toUnmodifiableList());

    if (undeterminedNodes.size() == 1) {
      route(undeterminedNodes.get(0), route, argumentDeque);
    } else {
      List<CommandRoute> alternateRoutes = undeterminedNodes.stream()
          .map(undeterminedNode -> {
            CommandRoute copyRoute = route.duplicate();
            route(undeterminedNode, copyRoute, new ArrayDeque<>(argumentDeque));
            return copyRoute;
          })
          .filter(CommandRoute::isValid)
          .collect(Collectors.toUnmodifiableList());

      route.clear();
      if (alternateRoutes.size() == 1) {
        route.addAll(alternateRoutes.get(0));
      } else if (alternateRoutes.size() > 1) {
        throw new CommandSolverException("Multiple command routes found", alternateRoutes);
      }
    }
  }
}
