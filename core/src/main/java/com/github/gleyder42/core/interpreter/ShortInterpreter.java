package com.github.gleyder42.core.interpreter;

import lombok.NonNull;

public class ShortInterpreter implements NumberInterpreter<Short> {

  @Override
  public Short parse(@NonNull String argument) {
    return Short.parseShort(argument);
  }
}
