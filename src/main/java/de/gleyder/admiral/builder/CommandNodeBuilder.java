package de.gleyder.admiral.builder;

import de.gleyder.admiral.CommandContext;
import de.gleyder.admiral.executors.Executor;
import de.gleyder.admiral.node.CommandNode;
import de.gleyder.admiral.node.key.NodeKey;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public abstract class CommandNodeBuilder<B extends CommandNodeBuilder, N extends CommandNode<? extends NodeKey>> {

  protected final N node;

  public B setExecutor(@NonNull Executor executor) {
    node.setExecutor(executor);
    return thisBuilder();
  }

  public B setRequired(@NonNull Predicate<CommandContext<? super Object>> required) {
    node.setRequired(required);
    return thisBuilder();
  }

  private B thisBuilder() {
    //noinspection unchecked
    return (B) this;
  }

  public N build() {
    return node;
  }
}
