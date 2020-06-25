package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.InterpreterStrategyNode;
import de.gleyder.admiral.annotation.executor.ExecutableMethod;
import de.gleyder.admiral.annotation.executor.MethodInterpreterStrategy;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodInterpreterStrategyProducer implements SimpleNodeProducer<InterpreterStrategyNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodInterpreterStrategy(new ExecutableMethod(instance, method));
  }

  @Override
  public String getValue(@NonNull InterpreterStrategyNode annotation) {
    return annotation.value();
  }
}
