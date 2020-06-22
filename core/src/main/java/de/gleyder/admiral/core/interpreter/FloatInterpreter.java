package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class FloatInterpreter implements NumberInterpreter<Float> {

  @Override
  public Float parse(@NonNull String argument) {
    return Float.parseFloat(argument);
  }
}
