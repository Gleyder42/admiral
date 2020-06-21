package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MethodInterpreterStrategy implements InterpreterStrategy {

  private final Object instance;
  private final Method method;

  @SuppressWarnings("unchecked")
  @SneakyThrows
  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument) {
    return (List<InterpreterResult<Object>>) method.invoke(instance, map, interpreter, inputArgument);
  }

}
