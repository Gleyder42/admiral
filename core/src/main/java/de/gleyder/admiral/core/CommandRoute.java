package de.gleyder.admiral.core;

import de.gleyder.admiral.core.node.key.NodeKey;
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
  private final List<CommandNode<? extends NodeKey>> nodeList;

  public CommandRoute(@NonNull List<CommandNode<? extends NodeKey>> nodeList) {
    this.nodeList = nodeList;
  }

  public CommandRoute() {
    this.nodeList = new ArrayList<>();
  }

  public void add(@NonNull CommandNode<? extends NodeKey> node) {
    nodeList.add(node);
  }

  public void addError(@NonNull Throwable error) {
    errorMessages.add(error);
  }

  public void addAll(@NonNull CommandRoute route) {
    this.nodeList.addAll(route.nodeList);
  }

  public CommandNode<NodeKey> get(int index) {
    //noinspection unchecked
    return (CommandNode<NodeKey>) nodeList.get(index);
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
