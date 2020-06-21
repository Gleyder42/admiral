package de.gleyder.admiral.annotation.builder.producer;

import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface NodeProducer<A extends Annotation> {

  Object produce(@NonNull Object instance, @NonNull Method method);
  String getKey(@NonNull A annotation);
}
