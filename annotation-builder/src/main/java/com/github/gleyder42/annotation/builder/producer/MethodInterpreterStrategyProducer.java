package com.github.gleyder42.annotation.builder.producer;

import com.github.gleyder42.annotation.InterpreterStrategyNode;
import com.github.gleyder42.annotation.executor.ExecutableMethod;
import com.github.gleyder42.annotation.executor.MethodInterpreterStrategy;
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
