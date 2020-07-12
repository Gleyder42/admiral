package de.gleyder.admiral.core;

import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.node.CommandNode;
import lombok.*;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * A route is a potential command.
 */
@EqualsAndHashCode
@ToString
public class CommandRoute {

  private final List<CommandError> errorMessages = new ArrayList<>();

  @TestOnly
  @Getter
  private final List<CommandNode> nodeList;

  @Getter
  private final ValueBag valueBag = new ValueBag();

  public CommandRoute(@NonNull List<CommandNode> nodeList) {
    this.nodeList = nodeList;
  }

  public CommandRoute() {
    this.nodeList = new ArrayList<>();
  }

  public boolean hasExecutor() {
    return nodeList.stream().anyMatch(node -> node.getExecutor().isPresent());
  }

  public void add(@NonNull CommandNode node) {
    nodeList.add(node);
  }

  public void addError(@NonNull CommandError error) {
    errorMessages.add(error);
  }

  public void addAll(@NonNull CommandRoute route) {
    this.nodeList.addAll(route.nodeList);
  }

  public void clearNodes() {
    nodeList.clear();
  }

  public CommandNode get(int index) {
    return nodeList.get(index);
  }

  public boolean isValid() {
    return errorMessages.isEmpty() && !nodeList.isEmpty();
  }

  public boolean isInvalid() {
    return !isValid();
  }

  public CommandRoute duplicate() {
    return new CommandRoute(new ArrayList<>(nodeList));
  }

  public List<CommandError> getErrors() {
    return List.copyOf(errorMessages);
  }

}
