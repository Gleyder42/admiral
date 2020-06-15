package de.gleyder.admiral.node;

import de.gleyder.admiral.CommandContext;
import de.gleyder.admiral.Executor;
import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.StringInterpreter;
import de.gleyder.admiral.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.interpreter.strategy.MergedStrategy;
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
public class CommandNode {

  private final Map<NodeKey, CommandNode> nodeMap = new HashMap<>();

  @Getter
  private final NodeKey key;

  @Setter
  private Predicate<CommandContext<? super Object>> required;

  @Setter
  private Executor executor;

  @Setter
  @Getter
  private InterpreterStrategy interpreterStrategy = new MergedStrategy();

  @Setter
  @Getter
  private Interpreter<?> interpreter = new StringInterpreter();

  public CommandNode(@NonNull NodeKey key) {
    this.key = key;
  }

  public void addNode(@NonNull CommandNode node) {
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

  public List<CommandNode> getNodes(@NonNull NodeKeyType type) {
    return nodeMap.values().stream()
        .filter(node -> node.getKey().getType() == type)
        .collect(Collectors.toUnmodifiableList());
  }

  public Optional<CommandNode> getNextNode(@NonNull String key) {
    return Optional.ofNullable(nodeMap.get(new StringNodeKey(key)));
  }

  @Override
  public String toString() {
    return key.get();
  }
}
