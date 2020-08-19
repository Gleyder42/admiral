package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

import java.util.Map;

public interface SimpleInterpreter<T> extends Interpreter<T> {

  InterpreterResult<T> interpret(@NonNull String argument);

  @Override
  default InterpreterResult<T> interpret(@NonNull Map<String, Object> ignored, @NonNull String argument) {
    return interpret(argument);
  }
}
