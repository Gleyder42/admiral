package de.gleyder.admiral;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class CommandSolverException extends RuntimeException {

  @Getter
  private final transient List<CommandRoute> routeList;

  public CommandSolverException(@NonNull String message, @NonNull List<CommandRoute> routeList) {
    super(message);
    this.routeList = routeList;
  }
}
