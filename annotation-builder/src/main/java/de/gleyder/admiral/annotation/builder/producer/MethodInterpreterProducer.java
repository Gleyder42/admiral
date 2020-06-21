package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.InterpreterNode;
import de.gleyder.admiral.annotation.executor.MethodInterpreter;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodInterpreterProducer implements NodeProducer<InterpreterNode>{
  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodInterpreter(instance, method);
  }

  @Override
  public String getKey(@NonNull InterpreterNode annotation) {
    return annotation.value();
  }
}
