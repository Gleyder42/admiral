package de.gleyder.admiral.node;

import de.gleyder.admiral.CommandContext;
import de.gleyder.admiral.Executor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * @author Eric
 * @version 18.06.2020
 */
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

  @SuppressWarnings("unchecked")
  private B thisBuilder() {
    return (B) this;
  }

  public N build() {
    return node;
  }
}
