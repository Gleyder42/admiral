package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class StringInterpreter implements SimpleInterpreter<String> {

  @Override
  public InterpreterResult<String> interpret(@NonNull String argument) {
    return InterpreterResult.ofValue(argument);
  }
}
