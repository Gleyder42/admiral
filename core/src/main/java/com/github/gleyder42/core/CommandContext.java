package com.github.gleyder42.core;

import com.github.gleyder42.core.executor.Executor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Provided if a command is executed via an {@link Executor}.
 */
public class CommandContext {

  private final Object source;

  @Getter
  private final ValueBag bag;

  public CommandContext(Object source, @NonNull ValueBag bag) {
    this.source = source;
    this.bag = bag;
  }

  public <T> T getSource() {
    return (T) source;
  }
}

