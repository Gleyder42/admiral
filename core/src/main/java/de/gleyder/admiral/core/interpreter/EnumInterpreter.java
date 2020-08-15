package de.gleyder.admiral.core.interpreter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnumInterpreter<T extends Enum<T>> implements SimpleInterpreter<T> {

  private final Class<T> enumType;

  @Override
  public InterpreterResult<T> interpret(@NonNull String argument) {
    return InterpreterResult.from(() -> Enum.valueOf(enumType, argument));
  }
}
