package de.gleyder.admiral.core;

import de.gleyder.admiral.core.node.CommandNode;
import lombok.*;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
public class CommandRoute {

  private final List<Throwable> errorMessages = new ArrayList<>();

  @TestOnly
  @Getter(AccessLevel.PUBLIC)
  private final List<CommandNode> nodeList;

  public CommandRoute(@NonNull List<CommandNode> nodeList) {
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

  public void addAll(@NonNull CommandRoute route) {
    this.nodeList.addAll(route.nodeList);
  }

  public void invalidate() {
    nodeList.clear();
    errorMessages.clear();
  }

  public CommandNode get(int index) {
    return nodeList.get(index);
  }

  public boolean isValid() {
    return errorMessages.isEmpty() && !nodeList.isEmpty();
  }

  public boolean isInvalid() {
    return !errorMessages.isEmpty() || nodeList.isEmpty();
  }

  public CommandRoute duplicate() {
    return new CommandRoute(new ArrayList<>(nodeList));
  }

  public List<Throwable> getErrors() {
    return List.copyOf(errorMessages);
  }
}
