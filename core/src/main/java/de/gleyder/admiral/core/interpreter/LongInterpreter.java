package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class LongInterpreter implements NumberInterpreter<Long> {

  @Override
  public Long parse(@NonNull String argument) {
    return Long.parseLong(argument);
  }
}
