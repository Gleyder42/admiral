package de.gleyder.admiral.core.builder;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.executor.Executor;
import de.gleyder.admiral.core.node.CommandNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public abstract class CommandNodeBuilder<B extends CommandNodeBuilder, N extends CommandNode> {

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
    return (B) this;
  }

  public N build() {
    return node;
  }
}
