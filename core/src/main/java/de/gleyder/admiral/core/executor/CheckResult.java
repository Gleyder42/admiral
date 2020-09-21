package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.error.LiteralCommandError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckResult {

  private static final CheckResult SUCCESSFUL = new CheckResult(null);

  public static CheckResult ofSuccessful() {
    return SUCCESSFUL;
  }

  public static CheckResult ofError(@NonNull CommandError error) {
    return new CheckResult(error);
  }

  public static CheckResult ofSimpleError(@NonNull BooleanSupplier supplier, @NonNull String errorMessage) {
    if (supplier.getAsBoolean()) {
      return CheckResult.ofSuccessful();
    } else {
      return CheckResult.ofError(LiteralCommandError.create().setMessage(errorMessage));
    }
  }

  private final CommandError error;

  public Optional<CommandError> getError() {
    return Optional.ofNullable(error);
  }

  public boolean wasSuccessful() {
    return error == null;
  }
}
