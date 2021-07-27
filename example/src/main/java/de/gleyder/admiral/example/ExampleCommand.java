package de.gleyder.admiral.example;

import de.gleyder.admiral.core.CommandDispatcher;
import de.gleyder.admiral.core.CommandRoute;
import de.gleyder.admiral.core.CommandSource;
import de.gleyder.admiral.core.builder.DynamicNodeBuilder;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.interpreter.CommonInterpreter;
import de.gleyder.admiral.core.interpreter.IntegerInterpreter;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;

import java.util.Collections;
import java.util.List;
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
          int amount = context.getBag().<Integer>get("amount").orElseThrow();
          String message = context.getBag().<String>get("message").orElseThrow();

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

    //Static nodes calc sum
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
          int number = context.getBag().<Integer>get("number").orElseThrow();
          int otherNumber = context.getBag().<Integer>get("otherNumber").orElseThrow();

          SenderSource senderSource = context.getSource();
          senderSource.sendMessage("Result: " + (number + otherNumber));
        })
        .build();

    //All commands
    StaticNode allCommands = new StaticNodeBuilder("allCommands")
        .setExecutor(context -> {
          List<CommandRoute> routes = dispatcher.getAllRoutes();

          routes.forEach(route -> System.out.println(String.join(" ", route.getNodeList().stream()
              .map(CommandNode::getKey)
              .collect(Collectors.toUnmodifiableList()))));
        })
        .build();

    //Add node returns the added node.
    calc.addNode(sumNode).addNode(numberNode).addNode(otherNumberNode);

    //Calc and allCommands nodes are registered
    dispatcher.registerCommand(calc);
    dispatcher.registerCommand(allCommands);
  }

  static class SenderSource implements CommandSource {

    private void sendMessage(String message) {
      System.out.println(message);
    }

    @Override
    public void sendFeedback(String message) {
      sendMessage(message);
    }
  }

}
