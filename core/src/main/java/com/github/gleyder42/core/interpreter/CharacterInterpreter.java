package com.github.gleyder42.core.interpreter;

import com.github.gleyder42.core.error.LiteralCommandError;
import lombok.NonNull;

public class CharacterInterpreter implements SimpleInterpreter<Character> {

  @Override
  public InterpreterResult<Character> interpret(@NonNull String argument) {
    if (argument.toCharArray().length == 1) {
      return InterpreterResult.ofValue(argument.toCharArray()[0]);
    } else {
      return InterpreterResult.ofError(LiteralCommandError.create().setMessage("String can only contain one character"));
    }
  }
}
