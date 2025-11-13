package com.github.gleyder42.annotation.builder.producer;

import com.github.gleyder42.annotation.CheckNode;
import com.github.gleyder42.annotation.executor.ExecutableMethod;
import com.github.gleyder42.annotation.executor.MethodCheck;
import java.lang.reflect.Method;
import lombok.NonNull;

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
