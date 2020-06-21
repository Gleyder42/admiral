package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.InterpreterStrategyNode;
import de.gleyder.admiral.annotation.executor.MethodInterpreterStrategy;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodInterpreterStrategyProducer implements NodeProducer<InterpreterStrategyNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodInterpreterStrategy(instance, method);
  }

  @Override
  public String getKey(@NonNull InterpreterStrategyNode annotation) {
    return annotation.value();
  }
}
