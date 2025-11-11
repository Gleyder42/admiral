package com.github.gleyder42.core.interpreter;

import lombok.NonNull;

public class DoubleInterpreter implements NumberInterpreter<Double> {

  @Override
  public Double parse(@NonNull String argument) {
    return Double.parseDouble(argument);
  }
}
