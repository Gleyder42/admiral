package com.github.gleyder42.core.node;

import com.github.gleyder42.core.executor.Check;
import com.github.gleyder42.core.executor.Executor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


/**
 * Base class for {@link StaticNode} and {@link DynamicNode}.
 */
public abstract sealed class CommandNode permits DynamicNode, StaticNode {

  private final Map<String, CommandNode> nodeMap = new HashMap<>();
  private final List<DynamicNode> dynamicNodeList = new ArrayList<>();

  @Getter
  private final String key;

  @Nullable
  @Getter

  @Setter
  private Check check;

  @Nullable
  @Getter
  @Setter
  private Executor executor;

  protected CommandNode(@NonNull String key) {
    this.key = key;
  }

  public CommandNode addNode(@NonNull CommandNode node) {
    switch (node) {
      case StaticNode staticNode -> {
        nodeMap.put(node.getKey(), staticNode);
        staticNode.getAliases().forEach(alias -> nodeMap.put(alias, staticNode));
      }
      case DynamicNode dynamicNode -> {
        dynamicNodeList.add(dynamicNode);
      }
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

  public List<DynamicNode> getDynamicNodes() {
    return dynamicNodeList;
  }

  @Nullable
  public CommandNode getNextNode(@NonNull String key) {
    return nodeMap.get(key);
  }

  @Override
  public String toString() {
    return key;
  }
}
