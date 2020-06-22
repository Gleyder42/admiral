package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;

public class ByteInterpreter implements NumberInterpreter<Byte> {

  @Override
  public Byte parse(@NonNull String argument) {
    return Byte.parseByte(argument);
  }
}
