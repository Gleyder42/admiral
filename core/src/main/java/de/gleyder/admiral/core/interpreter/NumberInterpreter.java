package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public interface NumberInterpreter<N extends Number> extends SimpleInterpreter<N> {

  N parse(@NonNull String argument);

  @Override
  default InterpreterResult<N> interpret(@NonNull String argument) {
    return InterpreterResult.from(() -> parse(argument));
  }
}
