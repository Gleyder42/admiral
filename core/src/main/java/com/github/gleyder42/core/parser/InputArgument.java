package com.github.gleyder42.core.parser;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a users inout.
 */
@ToString
@EqualsAndHashCode
public class InputArgument {

  @Getter
  private final List<String> inputs;

  public InputArgument() {
    this.inputs = new ArrayList<>();
  }

  public InputArgument(String input) {
    this.inputs = new ArrayList<>();
    this.inputs.add(input);
  }

  public boolean isSingle() {
    return inputs.size() == 1;
  }

  public String getMerged() {
    return String.join(" ", inputs);
  }
}
