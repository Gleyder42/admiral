package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.ExecutorNode;
import de.gleyder.admiral.annotation.executor.ExecutableMethod;
import de.gleyder.admiral.annotation.executor.MethodExecutor;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodExecutorProducer implements SimpleNodeProducer<ExecutorNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodExecutor(new ExecutableMethod(instance, method));
  }

  @Override
  public String getValue(@NonNull ExecutorNode annotation) {
    return annotation.value();
  }
}