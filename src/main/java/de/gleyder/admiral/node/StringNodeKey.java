package de.gleyder.admiral.node;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class StringNodeKey implements NodeKey {

  private final String key;

  public StringNodeKey(@NonNull String key) {
    this.key = key;
  }

  @Override
  public NodeKeyType getType() {
    return NodeKeyType.DETERMINED;
  }

  public String get() {
    return key;
  }
}
