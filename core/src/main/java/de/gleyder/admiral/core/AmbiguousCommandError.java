package de.gleyder.admiral.core;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AmbiguousCommandError implements CommandError {

  private static final String SIMPLE_MESSAGES = "Multiple commands found";

  private final List<CommandRoute> routeList;

  @Override
  public String getDetailed() {
    return getSimple() + ": " + String.join("\n", routeList.stream()
            .map(CommandRoute::toString)
            .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public String getSimple() {
    return SIMPLE_MESSAGES;
  }

}
