package de.gleyder.admiral.core.interpreter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpreterResult<T> {

  private final T value;
  private final Throwable throwable;

  public static <V> InterpreterResult<V> ofValue(@NonNull V value) {
    return new InterpreterResult<>(value, null);
  }

  public static <V> InterpreterResult<V> ofError(@NonNull Throwable throwable) {
    return new InterpreterResult<>(null, throwable);
  }

  public Optional<T> getValue() {
    return Optional.ofNullable(value);
  }

  public Optional<Throwable> getError() {
    return Optional.of(throwable);
  }

  public boolean failed() {
    return throwable != null;
  }

  public boolean succeeded() {
    return value != null;
  }
}
