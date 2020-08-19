package de.gleyder.admiral.annotation.builder.producer;

import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface SimpleNodeProducer<A extends Annotation> extends NodeProducer<A> {

  String getValue(@NonNull A annotation);

  @Override
  default String getKey(@NonNull A annotation, @NonNull Method method) {
    return !getValue(annotation).isEmpty() ? getValue(annotation) : method.getName();
  }
}
