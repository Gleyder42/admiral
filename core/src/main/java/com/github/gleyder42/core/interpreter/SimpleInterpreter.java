package com.github.gleyder42.core.interpreter;

import java.util.Map;
import lombok.NonNull;

public interface SimpleInterpreter<T> extends Interpreter<T> {

  InterpreterResult<T> interpret(@NonNull String argument);

  @Override
  default InterpreterResult<T> interpret(@NonNull Map<String, Object> ignored, @NonNull String argument) {
    return interpret(argument);
  }
}
