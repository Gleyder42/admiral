package de.gleyder.admiral.annotation.builder.producer;

import de.gleyder.admiral.annotation.RequiredNode;
import de.gleyder.admiral.annotation.executor.MethodRequired;
import lombok.NonNull;

import java.lang.reflect.Method;

public class MethodRequiredProducer implements SimpleNodeProducer<RequiredNode> {

  @Override
  public Object produce(@NonNull Object instance, @NonNull Method method) {
    return new MethodRequired(instance, method);
  }

  @Override
  public String getValue(@NonNull RequiredNode annotation) {
    return annotation.value();
  }

}
