package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.executor.Executor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class MethodExecutor implements Executor {

  private final Object instance;
  private final Method method;

  @Override
  public void execute(@NonNull CommandContext<?> context) {
    try {
      method.invoke(instance, context.getSource(), context.getBag());
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("An error occurred while executing a command", e);
    }
  }
}
