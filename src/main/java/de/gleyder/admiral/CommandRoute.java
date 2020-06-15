package de.gleyder.admiral;

import de.gleyder.admiral.node.CommandNode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class CommandRoute {

  private final List<Throwable> errorMessages = new ArrayList<>();
  private final List<CommandNode> nodeList;

  private CommandRoute(@NonNull List<CommandNode> nodeList) {
    this.nodeList = nodeList;
  }

  public CommandRoute() {
    this.nodeList = new ArrayList<>();
  }

  public void add(@NonNull CommandNode node) {
    nodeList.add(node);
  }

  public void addError(@NonNull Throwable error) {
    errorMessages.add(error);
  }

  public void addErrors(@NonNull List<Throwable> throwableList) {
    errorMessages.addAll(throwableList);
  }

  public void addAll(@NonNull CommandRoute route) {
    this.nodeList.addAll(route.nodeList);
  }

  public CommandNode get(int index) {
    return nodeList.get(index);
  }

  public void clear() {
    nodeList.clear();
  }

  public boolean isValid() {
    return !nodeList.isEmpty();
  }

  public boolean isInvalid() {
    return nodeList.isEmpty();
  }

  public CommandRoute duplicate() {
    return new CommandRoute(new ArrayList<>(nodeList));
  }

  public List<Throwable> getErrors() {
    return List.copyOf(errorMessages);
  }
}
