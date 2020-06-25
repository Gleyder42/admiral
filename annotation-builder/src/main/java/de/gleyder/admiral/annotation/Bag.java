package de.gleyder.admiral.annotation;

public @interface Bag {

  boolean nullable() default false;
  String value();
}
