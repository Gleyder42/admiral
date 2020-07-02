package de.gleyder.admiral.core;

import lombok.Getter;
import lombok.NonNull;

public class CommandContext {

  private final Object source;

  @Getter
  private final ValueBag bag;

  public CommandContext(Object source, @NonNull ValueBag bag) {
    this.source = source;
    this.bag = bag;
  }

  public <T> T getSource(Class<T> tClass) {
    return (T) source;
  }

  public <T> T getSource() {
    return (T) source;
  }
}

