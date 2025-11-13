package com.github.gleyder42.core.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a users inout.
 */
public record InputArgument(List<String> inputs) {

  public InputArgument() {
    this(new ArrayList<>());
  }

  public InputArgument(String input) {
    this(new ArrayList<>());
    this.inputs.add(input);
  }

  public boolean isSingle() {
    return inputs.size() == 1;
  }

  public String getMerged() {
    return String.join(" ", inputs);
  }
}
