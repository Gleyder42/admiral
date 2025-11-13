package com.github.gleyder42.annotation.builder.producer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.NonNull;

public interface NodeProducer<A extends Annotation> {

  Object produce(@NonNull Object instance, @NonNull Method method);

  String getKey(@NonNull A annotation, @NonNull Method method);
}
