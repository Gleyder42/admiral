package de.gleyder.admiral.core;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class CommandDispatcherException extends RuntimeException {

  @Getter
  private final List<CommandRoute> routeList;

  public CommandDispatcherException(@NonNull String message, @NonNull List<CommandRoute> routeList) {
    super(message);
    this.routeList = routeList;
  }
}
