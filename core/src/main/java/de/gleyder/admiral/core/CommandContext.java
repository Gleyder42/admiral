package de.gleyder.admiral.core;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CommandContext<T> {

  private final T source;
  private final ValueBag bag;

  public CommandContext(@NonNull T source, @NonNull ValueBag bag) {
    this.source = source;
    this.bag = bag;
  }
}