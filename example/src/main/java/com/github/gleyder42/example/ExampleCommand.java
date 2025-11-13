package com.github.gleyder42.example;

import com.github.gleyder42.core.CommandDispatcher;
import com.github.gleyder42.core.CommandRoute;
import com.github.gleyder42.core.builder.DynamicNodeBuilder;
import com.github.gleyder42.core.builder.StaticNodeBuilder;
import com.github.gleyder42.core.error.CommandError;
import com.github.gleyder42.core.interpreter.CommonInterpreter;
import com.github.gleyder42.core.interpreter.IntegerInterpreter;
import com.github.gleyder42.core.node.CommandNode;
import com.github.gleyder42.core.node.DynamicNode;
import com.github.gleyder42.core.node.StaticNode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
 * This simple application is used to demonstrate how to use commands.
 * <p>
 * Following commands are created:
 * <p>
 * allCommands - Displays all commands
 * stop, abort - Stops the application
 * calc sum <int> <int> - Adds two integers
 */
public class ExampleCommand {

  public static void main(String[] args) {
    //Creates a dispatcher
    CommandDispatcher dispatcher = new CommandDispatcher();
    Scanner scanner = new Scanner(System.in);
    var booleanWrapper = new Object() {
      boolean running = true;
    };

    //Creates a command node abort.
    StaticNode stopNode = new StaticNodeBuilder("stop")
        .addAlias("abort")
        .setExecutor(context -> booleanWrapper.running = false)
        .build();

    //Adds the stop node to the dispatcher
    dispatcher.registerCommand(stopNode);
    addCommand(dispatcher);
    readmeExample(dispatcher);

    while (booleanWrapper.running) {
      String input = scanner.nextLine();
      List<CommandError> dispatch = dispatcher.dispatch(input, new SenderSource(), Collections.emptyMap());
      dispatch.forEach(error -> System.out.println(error.getDetailed()));
    }
  }

  public static void readmeExample(CommandDispatcher dispatcher) {
    StaticNode echoNode = new StaticNodeBuilder("echo")
        .setExecutor(context -> {
          int amount = Objects.requireNonNullElse(context.getBag().get("amount"), 0);
          String message = context.getBag().get("message");

          for (int i = 0; i < amount; i++) {
            System.out.println("Nr. " + i + " " + message);
          }
        })
        .build();
    DynamicNode amountNode = new DynamicNodeBuilder("amount").setInterpreter(CommonInterpreter.INT).build();
    DynamicNode messageNode = new DynamicNodeBuilder("message").build();

    echoNode.addNode(amountNode).addNode(messageNode);

    dispatcher.registerCommand(echoNode);
  }

  /*
   * Adds the commands to the dispatcher. A dedicated method improves code readability here.
   */
  private static void addCommand(CommandDispatcher dispatcher) {
    /*
     * Calculation Nodes
     */

    // Static nodes calc sum
    StaticNode calc = new StaticNodeBuilder("calc")
        .build();
    StaticNode sumNode = new StaticNode("sum");

    // Dynamic nodes <int> <int>
    DynamicNode numberNode = new DynamicNodeBuilder("number")
        .setInterpreter(new IntegerInterpreter())
        .build();
    DynamicNode otherNumberNode = new DynamicNodeBuilder("otherNumber")
        .setInterpreter(new IntegerInterpreter())
        .setExecutor(context -> {
          int number = Objects.requireNonNull(context.getBag().<Integer>get("number"));
          int otherNumber = Objects.requireNonNull(context.getBag().<Integer>get("otherNumber"));

          SenderSource senderSource = context.getSource();
          senderSource.sendMessage("Result: " + (number + otherNumber));
        })
        .build();

    // All commands
    StaticNode allCommands = new StaticNodeBuilder("allCommands")
        .setExecutor(context -> {
          List<CommandRoute> routes = dispatcher.getAllRoutes();

          routes.forEach(route -> System.out.println(String.join(" ", route.getNodeList().stream()
              .map(CommandNode::getKey)
              .toList())));
        })
        .build();

    // Add node returns the added node.
    calc.addNode(sumNode).addNode(numberNode).addNode(otherNumberNode);

    // Calc and allCommands nodes are registered
    dispatcher.registerCommand(calc);
    dispatcher.registerCommand(allCommands);
  }

  static class SenderSource {

    private void sendMessage(String message) {
      System.out.println(message);
    }

  }

}
