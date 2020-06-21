package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;

@RequiredArgsConstructor
public class MethodInterpreter implements Interpreter<Object> {

  private final Object instance;
  private final Method method;

  @SuppressWarnings("unchecked")
  @SneakyThrows
  @Override
  public InterpreterResult<Object> interpret(@NonNull Map<String, Object> map, @NonNull String argument) {
    return (InterpreterResult<Object>) method.invoke(instance, map, argument);
  }

}
