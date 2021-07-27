package de.gleyder.admiral.core.help;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.CommandRoute;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.StaticNode;

import java.util.stream.Collectors;

public class HelpCommand {

  private final String help;

  public HelpCommand(StaticNode rootNode) {
    this.help = rootNode.getAllRoutes().stream()
        .map(this::toCommandString)
        .collect(Collectors.joining("\n"));
    var helpNode = new StaticNodeBuilder("help")
        .setExecutor(this::execute)
        .build();
    rootNode.addNode(helpNode);
  }

  private void execute(CommandContext context) {
    context.getSource().sendFeedback(help);
  }

  private String toCommandString(CommandRoute route) {
    var message = route.getNodeList().stream()
        .flatMap(commandNode -> commandNode.getDescription().stream())
        .collect(Collectors.joining("\n"));

    var nodesToString = route.getNodeList().stream().map(CommandNode::getKey).collect(Collectors.joining(", "));
    if (message.isEmpty()) {
      return nodesToString + "\n";
    }
    return nodesToString + "\n" + "Info: \n" + message + "\n";
  }
}
