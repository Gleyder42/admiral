package de.gleyder.admiral.interpreter;

import lombok.NonNull;

public class StringInterpreter implements Interpreter<String> {

  @Override
  public InterpreterResult<String> interpret(@NonNull String argument) {
    return InterpreterResult.createSuccessful(argument);
  }
}
