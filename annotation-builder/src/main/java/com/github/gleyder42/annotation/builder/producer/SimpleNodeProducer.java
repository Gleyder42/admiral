package com.github.gleyder42.annotation.builder.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.NonNull;

public interface SimpleNodeProducer<A extends Annotation> extends NodeProducer<A> {

  String getValue(@NonNull A annotation);

  @Override
  default String getKey(@NonNull A annotation, @NonNull Method method) {
    return !getValue(annotation).isEmpty() ? getValue(annotation) : method.getName();
  }
}
