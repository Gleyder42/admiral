package de.gleyder.admiral.node;

import lombok.NonNull;

public interface NodeKey {

  String get();
  NodeKeyType getType();

  static NodeKey ofStatic(@NonNull String key) {
    return new StringNodeKey(key);
  }

  static NodeKey ofDynamic(@NonNull Class<?> aClass, @NonNull String key) {
    return new TypeNoteKey<>(aClass, key);
  }
}
