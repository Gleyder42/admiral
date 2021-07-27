package de.gleyder.admiral.core.builder;

import de.gleyder.admiral.core.executor.Check;
import de.gleyder.admiral.core.executor.Executor;
import de.gleyder.admiral.core.node.CommandNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public abstract class CommandNodeBuilder<B extends CommandNodeBuilder, N extends CommandNode> {

  protected final N node;

  public B setExecutor(@NonNull Executor executor) {
    node.setExecutor(executor);
    return thisBuilder();
  }

  public B setCheck(@NonNull Check check) {
    node.setCheck(check);
    return thisBuilder();
  }

  public B setDescription(@NonNull String description) {
    node.setDescription(description);
    return thisBuilder();
  }

  private B thisBuilder() {
    return (B) this;
  }

  public N build() {
    return node;
  }
}
