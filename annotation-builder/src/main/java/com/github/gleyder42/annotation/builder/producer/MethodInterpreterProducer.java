package com.github.gleyder42.annotation.builder.producer;

import com.github.gleyder42.annotation.InterpreterNode;
import com.github.gleyder42.annotation.executor.ExecutableMethod;
import com.github.gleyder42.annotation.executor.MethodInterpreter;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodInterpreterProducer implements SimpleNodeProducer<InterpreterNode> {
  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodInterpreter(new ExecutableMethod(instance, method));
  }

  @Override
  public String getValue(@NonNull InterpreterNode annotation) {
    return annotation.value();
  }
}
