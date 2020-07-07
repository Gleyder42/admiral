package de.gleyder.admiral.core.node;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.executor.Check;
import de.gleyder.admiral.core.executor.Executor;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

@Accessors(chain = true)
public abstract class CommandNode {

  private final Map<String, CommandNode> nodeMap = new HashMap<>();
  private final List<DynamicNode> dynamicNodeList = new ArrayList<>();

  @Getter
  private final String key;

  @Setter
  private Check check;

  @Setter
  private Executor executor;

  public CommandNode(@NonNull String key) {
    this.key = key;
  }

  public void onCommandProcess(@NonNull CommandContext context, @NonNull Map<String, Object> interpreterMap, @NonNull InputArgument inputArgument) { }

  public CommandNode addNode(@NonNull CommandNode node) {
    if (node instanceof StaticNode) {
      StaticNode staticNode = (StaticNode) node;
      nodeMap.put(node.getKey(), staticNode);
      staticNode.getAliases().forEach(alias -> nodeMap.put(alias, staticNode));
    } else {
      dynamicNodeList.add((DynamicNode) node);
    }
    return node;
  }

  public Set<CommandNode> getAllNodes() {
    Set<CommandNode> nodeList = new HashSet<>(nodeMap.values());
    nodeList.addAll(dynamicNodeList);
    return nodeList;
  }

  public boolean isLeaf() {
    return nodeMap.isEmpty() && dynamicNodeList.isEmpty();
  }

  public Optional<Check> getCheck() {
    return Optional.ofNullable(check);
  }

  public Optional<Executor> getExecutor() {
    return Optional.ofNullable(executor);
  }

  public List<DynamicNode> getDynamicNodes() {
    return dynamicNodeList;
  }

  public Optional<CommandNode> getNextNode(@NonNull String key) {
    return Optional.ofNullable(nodeMap.get(key));
  }

  @Override
  public String toString() {
    return key;
  }
}
