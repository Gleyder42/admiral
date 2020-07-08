package de.gleyder.admiral.core.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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
