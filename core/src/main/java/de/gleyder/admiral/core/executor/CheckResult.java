package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.CommandError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResult {

  private static final CheckResult SUCCESSFUL = new CheckResult(null);

  public static CheckResult ofSuccessful() {
    return SUCCESSFUL;
  }

  public static CheckResult ofError(@NonNull CommandError error) {
    return new CheckResult(error);
  }

  private CommandError error;

  public Optional<CommandError> getError() {
    return Optional.of(error);
  }

  public boolean wasSuccessful() {
    return error == null;
  }

}
