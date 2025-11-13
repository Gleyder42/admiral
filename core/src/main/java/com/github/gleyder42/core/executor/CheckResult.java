package com.github.gleyder42.core.executor;

import com.github.gleyder42.core.error.CommandError;
import com.github.gleyder42.core.error.LiteralCommandError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

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

  @Nullable
  private final CommandError error;

  @Nullable
  public CommandError getError() {
    return error;
  }

  public boolean wasSuccessful() {
    return error == null;
  }
}
