package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class CharacterInterpreter implements SimpleInterpreter<Character> {

  @Override
  public InterpreterResult<Character> interpret(@NonNull String argument) {
    if (argument.toCharArray().length == 1) {
      return InterpreterResult.createSuccessful(argument.toCharArray()[0]);
    } else {
      return InterpreterResult.createError(new IllegalStateException("String can only contain one character"));
    }
  }
}
