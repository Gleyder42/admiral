package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class IntegerInterpreter implements NumberInterpreter<Integer> {

  @Override
  public Integer parse(@NonNull String argument) {
    return Integer.parseInt(argument);
  }

}

