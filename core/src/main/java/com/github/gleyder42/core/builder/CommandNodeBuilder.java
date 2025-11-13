package com.github.gleyder42.core.builder;

import com.github.gleyder42.core.executor.Check;
import com.github.gleyder42.core.executor.Executor;
import com.github.gleyder42.core.node.CommandNode;
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

  private B thisBuilder() {
    return (B) this;
  }

  public N build() {
    return node;
  }
}
