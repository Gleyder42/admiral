package com.github.gleyder42.annotation.executor;

import com.github.gleyder42.annotation.supplier.ArgumentSupplier;
import com.github.gleyder42.core.interpreter.Interpreter;
import com.github.gleyder42.core.interpreter.InterpreterResult;
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
