package com.github.gleyder42.core.interpreter.strategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommonStrategy {

  MERGED(new MergedStrategy()),
  SINGLE(new SingleStrategy());

  private final InterpreterStrategy strategy;

  public InterpreterStrategy get() {
    return strategy;
  }
}
