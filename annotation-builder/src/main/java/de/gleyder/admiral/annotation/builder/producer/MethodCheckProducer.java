package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.CheckNode;
import de.gleyder.admiral.annotation.executor.ExecutableMethod;
import de.gleyder.admiral.annotation.executor.MethodCheck;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodCheckProducer implements SimpleNodeProducer<CheckNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodCheck(new ExecutableMethod(instance, method));
  }

  @Override
  public String getValue(@NonNull CheckNode annotation) {
    return annotation.value();
  }

}
