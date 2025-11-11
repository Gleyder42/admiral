package com.github.gleyder42.annotation.builder.producer;

import com.github.gleyder42.annotation.ExecutorNode;
import com.github.gleyder42.annotation.executor.ExecutableMethod;
import com.github.gleyder42.annotation.executor.MethodExecutor;
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