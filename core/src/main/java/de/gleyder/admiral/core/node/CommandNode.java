package de.gleyder.admiral.core.node;

import de.gleyder.admiral.core.CommandRoute;
import de.gleyder.admiral.core.executor.Check;
import de.gleyder.admiral.core.executor.Executor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

/**
 * Base class for {@link StaticNode} and {@link DynamicNode}.
 */
public abstract class CommandNode {

  private final Map<String, CommandNode> nodeMap = new HashMap<>();
  private final List<DynamicNode> dynamicNodeList = new ArrayList<>();

  @Getter
  private final String key;

  @Setter
  private Check check;

  @Setter
  private Executor executor;

  @Setter
  private String description;

  protected CommandNode(@NonNull String key) {
    this.key = key;
  }

  public CommandNode addNode(@NonNull CommandNode node) {
    if (node instanceof StaticNode) {
      var staticNode = (StaticNode) node;
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

  public List<CommandRoute> getAllRoutes() {
    List<CommandRoute> routeList = new ArrayList<>();
    findAllRoutes(routeList, new CommandRoute(), this);
    return routeList;
  }

  private void findAllRoutes(@NonNull List<CommandRoute> routeList, @NonNull CommandRoute route, @NonNull CommandNode node) {
    route.add(node);
    node.getAllNodes().forEach(nextNode -> findAllRoutes(routeList, route.duplicate(), nextNode));

    if (node.isLeaf() || (!node.isLeaf() && route.hasExecutor())) {
      routeList.add(route);
    }
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

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<CommandNode> getNextNode(@NonNull String key) {
    return Optional.ofNullable(nodeMap.get(key));
  }

  @Override
  public String toString() {
    return key;
  }
}
