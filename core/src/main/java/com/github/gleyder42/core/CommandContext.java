package com.github.gleyder42.core;

import com.github.gleyder42.core.executor.Executor;
import lombok.NonNull;

/**
 * Provided if a command is executed via an {@link Executor}.
 */
public record CommandContext(Object source, @NonNull ValueBag bag) {

  public <T> T getSource() {
    return (T) source;
  }
}

