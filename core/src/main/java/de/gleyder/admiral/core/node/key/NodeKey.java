package de.gleyder.admiral.core.node.key;

import lombok.NonNull;

public interface NodeKey {

  String get();

  static StringNodeKey ofStatic(@NonNull String key) {
    return new StringNodeKey(key);
  }

  static <T> TypeNodeKey<T> ofDynamic(@NonNull Class<T> aClass, @NonNull String key) {
    return new TypeNodeKey<>(aClass, key);
  }
}
