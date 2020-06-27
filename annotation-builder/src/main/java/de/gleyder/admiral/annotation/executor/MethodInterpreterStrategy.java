package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import de.gleyder.admiral.annotation.supplier.ValueBagArgumentSupplier;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MethodInterpreterStrategy implements InterpreterStrategy {

  private final ExecutableMethod method;

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument) {
    return method.invokeReturn(List.of(interpreter, inputArgument), ArgumentSupplier.ofMap(map));
  }
}
