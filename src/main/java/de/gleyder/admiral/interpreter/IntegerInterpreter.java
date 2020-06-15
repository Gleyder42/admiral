package de.gleyder.admiral.interpreter;

import lombok.NonNull;

public class IntegerInterpreter implements Interpreter<Integer> {

  @Override
  public InterpreterResult<Integer> interpret(@NonNull String argument) {
    try {
      return InterpreterResult.createSuccessful(Integer.parseInt(argument));
    } catch (NumberFormatException exception) {
      return InterpreterResult.createError(exception);
    }
  }
}
