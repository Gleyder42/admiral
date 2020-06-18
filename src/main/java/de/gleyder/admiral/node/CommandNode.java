package de.gleyder.admiral.node;

import de.gleyder.admiral.CommandContext;
import de.gleyder.admiral.executors.Executor;
import de.gleyder.admiral.node.key.NodeKey;
import de.gleyder.admiral.node.key.StringNodeKey;
import de.gleyder.admiral.parser.InputArgument;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Accessors(chain = true)
public abstract class CommandNode<K extends NodeKey> {

  private final Map<NodeKey, CommandNode<?>> nodeMap = new HashMap<>();

  @Getter
  private final K key;

  @Setter
  private Predicate<CommandContext<? super Object>> required;

  @Setter
  private Executor executor;

  public CommandNode(@NonNull K key) {
    this.key = key;
  }

  public void onCommandCycle(@NonNull CommandContext<?> context, @NonNull InputArgument inputArgument) { }

  public void addNode(@NonNull CommandNode<?> node) {
    this.nodeMap.put(node.getKey(), node);
  }

  public boolean isLeaf() {
    return nodeMap.isEmpty();
  }

  public Optional<Predicate<CommandContext<Object>>> getRequired() {
    return Optional.ofNullable(required);
  }

  public Optional<Executor> getExecutor() {
    return Optional.ofNullable(executor);
  }

  public List<DynamicNode> getDynamicNodes() {
    return nodeMap.values().stream()
            .filter(node -> node instanceof DynamicNode)
            .map(node -> (DynamicNode) node)
            .collect(Collectors.toUnmodifiableList());
  }

  public Optional<CommandNode<NodeKey>> getNextNode(@NonNull String key) {
    //noinspection unchecked
    return Optional.ofNullable((CommandNode<NodeKey>) nodeMap.get(new StringNodeKey(key)));
  }

  @Override
  public String toString() {
    return key.get();
  }

}
