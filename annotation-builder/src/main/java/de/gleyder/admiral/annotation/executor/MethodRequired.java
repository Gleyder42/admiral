package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.core.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class MethodRequired implements Predicate<CommandContext<? super Object>> {

  private final Object instance;
  private final Method method;

  @SneakyThrows
  @Override
  public boolean test(CommandContext<? super Object> context) {
    return (boolean) method.invoke(instance, context.getSource(), context.getBag());
  }

}
