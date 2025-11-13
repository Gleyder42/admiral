package com.github.gleyder42.annotation.executor;

import com.github.gleyder42.annotation.supplier.ArgumentSupplier;
import com.github.gleyder42.core.interpreter.Interpreter;
import com.github.gleyder42.core.interpreter.InterpreterResult;
import com.github.gleyder42.core.interpreter.strategy.InterpreterStrategy;
import com.github.gleyder42.core.parser.InputArgument;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodInterpreterStrategy implements InterpreterStrategy {

  private final ExecutableMethod method;

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter,
                                              @NonNull InputArgument inputArgument) {
    return method.invokeReturn(List.of(interpreter, inputArgument), ArgumentSupplier.ofMap(map));
  }
}
