package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class BooleanInterpreter implements SimpleInterpreter<Boolean> {

  @Override
  public InterpreterResult<Boolean> interpret(@NonNull String argument) {
    if (argument.equalsIgnoreCase("true") || argument.equalsIgnoreCase("false")) {
      return InterpreterResult.ofValue(Boolean.parseBoolean(argument));
    } else {
      return InterpreterResult.ofError(new IllegalArgumentException(argument + " is not a boolean"));
    }
  }

}
