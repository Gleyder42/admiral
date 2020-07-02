package de.gleyder.admiral.example;

import de.gleyder.admiral.core.CommandDispatcher;
import de.gleyder.admiral.core.CommandRoute;
import de.gleyder.admiral.core.builder.DynamicNodeBuilder;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.executor.Executor;
import de.gleyder.admiral.core.interpreter.IntegerInterpreter;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ExampleCommand {

  public static void main(String[] args) {
    //Creates a dispatcher
    CommandDispatcher dispatcher = new CommandDispatcher();
    Scanner scanner = new Scanner(System.in);
    var booleanWrapper = new Object() {
      boolean running = true;
    };

    StaticNode stopNode = new StaticNodeBuilder("stop")
            .addAlias("abort")
            .setExecutor(context -> booleanWrapper.running = false)
            .build();

    dispatcher.registerCommand(stopNode);
    addCommand(dispatcher);

    while (booleanWrapper.running) {
      String input = scanner.nextLine();
      List<Throwable> dispatch = dispatcher.dispatch(input, new SenderSource(), Collections.emptyMap());
      dispatch.forEach(System.out::println);
    }
  }

  private static void addCommand(CommandDispatcher dispatcher) {
    Executor executor = context -> {
      int number = context.getBag().get("number", Integer.class).orElseThrow();
      int otherNumber = context.getBag().get("otherNumber", Integer.class).orElseThrow();

      SenderSource senderSource = context.getSource();
      senderSource.sendMessage("Result: " + (number + otherNumber));
    };

    StaticNode sumNode = new StaticNode("sum");
    DynamicNode number = new DynamicNodeBuilder("number")
            .setInterpreter(new IntegerInterpreter())
            .build();
    sumNode.addNode(number);
    DynamicNode otherNumberNode = new DynamicNodeBuilder("otherNumber")
            .setInterpreter(new IntegerInterpreter())
            .setExecutor(executor)
            .build();
    number.addNode(otherNumberNode);

    StaticNode calc = new StaticNodeBuilder("calc")
            .build();
    calc.addNode(sumNode);
    dispatcher.registerCommand(calc);


    StaticNode allCommands = new StaticNodeBuilder("allCommands")
            .setExecutor(context -> {
              List<CommandRoute> routes = dispatcher.getAllRoutes();

              routes.stream().forEach(route -> System.out.println(String.join(" ", route.getNodeList().stream().map(CommandNode::getKey).collect(Collectors.toUnmodifiableList()))));
            })
            .build();

    dispatcher.registerCommand(allCommands);
  }

  static class SenderSource {

    private void sendMessage(String message) {
      System.out.println(message);
    }
  }

}
