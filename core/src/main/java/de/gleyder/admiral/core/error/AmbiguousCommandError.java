package de.gleyder.admiral.core.error;

import de.gleyder.admiral.core.CommandRoute;
import de.gleyder.admiral.core.error.CommandError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CommandError} for the case in
 * which multiple command routes were found.
 */
@RequiredArgsConstructor
public class AmbiguousCommandError implements CommandError {

  private static final String SIMPLE_MESSAGES = "Multiple commands found";

  @Getter
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
