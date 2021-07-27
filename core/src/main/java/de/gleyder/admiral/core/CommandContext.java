package de.gleyder.admiral.core;

import lombok.Getter;
import lombok.NonNull;

/**
 * Provided if a command is executed via an {@link de.gleyder.admiral.core.executor.Executor}.
 */
public class CommandContext {

  private final CommandSource source;

  @Getter
  private final ValueBag bag;

  public CommandContext(CommandSource source, @NonNull ValueBag bag) {
    this.source = source;
    this.bag = bag;
  }

  public <T extends CommandSource> T getSource() {
    return (T) source;
  }
}

