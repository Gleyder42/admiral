package de.gleyder.admiral.core;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class AmbiguousCommandRouteException extends RuntimeException {

  @Getter
  private final List<CommandRoute> routeList;

  public AmbiguousCommandRouteException(@NonNull String message, List<CommandRoute> routeList) {
    super(message);
    this.routeList = routeList;
  }
}
