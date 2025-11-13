package com.github.gleyder42.core.interpreter;

import lombok.NonNull;

public class StringInterpreter implements SimpleInterpreter<String> {

  @Override
  public InterpreterResult<String> interpret(@NonNull String argument) {
    return InterpreterResult.ofValue(argument);
  }
}
