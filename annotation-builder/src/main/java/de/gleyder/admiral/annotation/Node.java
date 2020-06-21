package de.gleyder.admiral.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Node {

  String required() default "";
  String interpreter() default "";
  String strategy() default "merged";
  String name();
  Class<?> type() default NoType.class;

}
