package com.github.gleyder42.core.interpreter;

import lombok.NonNull;

public class ByteInterpreter implements NumberInterpreter<Byte> {

  @Override
  public Byte parse(@NonNull String argument) {
    return Byte.parseByte(argument);
  }
}
