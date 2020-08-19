package de.gleyder.admiral.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Node {

  String required() default "";

  String executor() default "";

  String[] aliases() default {};

  String interpreter() default "";

  String strategy() default "";

  String value();
}
