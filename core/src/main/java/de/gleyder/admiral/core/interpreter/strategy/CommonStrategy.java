package de.gleyder.admiral.core.interpreter.strategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommonStrategy {

  MERGED(new MergedStrategy()),
  SINGLE(new SingleStrategy())
  ;

  private final InterpreterStrategy strategy;

  public InterpreterStrategy get() {
    return strategy;
  }

}
