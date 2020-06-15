package de.gleyder.admiral.node;

import lombok.NonNull;

public interface NodeKey {

  String get();
  NodeKeyType getType();

  static NodeKey ofString(@NonNull String string) {
    return new StringNodeKey(string);
  }

}
