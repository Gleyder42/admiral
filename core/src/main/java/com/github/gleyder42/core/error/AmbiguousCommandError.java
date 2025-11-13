package com.github.gleyder42.core.error;

import com.github.gleyder42.core.CommandRoute;
import com.github.gleyder42.core.Messages;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link CommandError} for the case in
 * which multiple command routes were found.
 */
@RequiredArgsConstructor
public class AmbiguousCommandError implements CommandError {

  @Getter
  private final List<CommandRoute> routeList;

  @Override
  public String getDetailed() {
    return getSimple() + ": " + routeList.stream()
            .map(CommandRoute::toString)
            .collect(Collectors.joining("\n"));
  }

  @Override
  public String getSimple() {
    return Messages.MULTIPLE_COMMANDS_FOUND.get();
  }
}
