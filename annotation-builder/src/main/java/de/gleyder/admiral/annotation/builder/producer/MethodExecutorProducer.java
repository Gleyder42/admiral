package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.ExecutorNode;
import de.gleyder.admiral.annotation.executor.MethodExecutor;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodExecutorProducer implements NodeProducer<ExecutorNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodExecutor(instance, method);
  }

  @Override
  public String getKey(@NonNull ExecutorNode annotation) {
    return annotation.value();
  }
}