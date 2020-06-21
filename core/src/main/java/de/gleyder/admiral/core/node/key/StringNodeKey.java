package de.gleyder.admiral.core.node.key;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class StringNodeKey implements NodeKey {

  private final String key;

  public StringNodeKey(@NonNull String key) {
    this.key = key;
  }

  public String get() {
    return key;
  }
}
