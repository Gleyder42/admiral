package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MethodInterpreter implements Interpreter<Object> {

  private final ExecutableMethod method;

  @Override
  public InterpreterResult<Object> interpret(@NonNull Map<String, Object> map, @NonNull String argument) {
    return method.invokeReturn(List.of(argument), ArgumentSupplier.ofMap(map));
  }
}
