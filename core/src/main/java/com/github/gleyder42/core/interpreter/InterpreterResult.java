package com.github.gleyder42.core.interpreter;

import com.github.gleyder42.core.error.CommandError;
import com.github.gleyder42.core.error.ThrowableCommandError;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpreterResult<T> {

  @Getter
  @Nullable
  private final T value;

  @Getter
  @Nullable
  private final CommandError error;

  public static <V> InterpreterResult<V> ofValue(@NonNull V value) {
    return new InterpreterResult<>(value, null);
  }

  public static <V> InterpreterResult<V> ofError(@NonNull CommandError error) {
    return new InterpreterResult<>(null, error);
  }

  public static <V> InterpreterResult<V> from(@NonNull Supplier<V> supplier) {
    try {
      return InterpreterResult.ofValue(supplier.get());
    } catch (Exception exception) {
      return InterpreterResult.ofError(new ThrowableCommandError(exception));
    }
  }

  public boolean failed() {
    return error != null;
  }

  public boolean succeeded() {
    return value != null;
  }
}

