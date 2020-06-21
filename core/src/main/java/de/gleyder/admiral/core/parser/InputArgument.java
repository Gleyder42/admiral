package de.gleyder.admiral.core.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sender input
 */
@ToString
@EqualsAndHashCode
public class InputArgument {

  @Getter
  List<String> inputs;

  /**
   * Creates an InputArgument
   */
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
