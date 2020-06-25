package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.InterpreterNode;
import de.gleyder.admiral.annotation.executor.ExecutableMethod;
import de.gleyder.admiral.annotation.executor.MethodInterpreter;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodInterpreterProducer implements SimpleNodeProducer<InterpreterNode>{
  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodInterpreter(new ExecutableMethod(instance, method));
  }

  @Override
  public String getValue(@NonNull InterpreterNode annotation) {
    return annotation.value();
  }
}
