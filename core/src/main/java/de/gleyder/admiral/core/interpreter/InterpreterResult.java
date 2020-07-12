package de.gleyder.admiral.core.interpreter;

import de.gleyder.admiral.core.error.CommandError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpreterResult<T> {

  private final T value;
  private final CommandError error;

  public static <V> InterpreterResult<V> ofValue(@NonNull V value) {
    return new InterpreterResult<>(value, null);
  }

  public static <V> InterpreterResult<V> ofError(@NonNull CommandError error) {
    return new InterpreterResult<>(null, error);
  }

  public Optional<T> getValue() {
    return Optional.ofNullable(value);
  }

  public Optional<CommandError> getError() {
    return Optional.of(error);
  }

  public boolean failed() {
    return error != null;
  }

  public boolean succeeded() {
    return value != null;
  }
}
