package de.gleyder.admiral.node;

import lombok.NonNull;

public interface NodeKey {

  String get();

  static StringNodeKey ofStatic(@NonNull String key) {
    return new StringNodeKey(key);
  }

  static <T> TypeNoteKey<T> ofDynamic(@NonNull Class<T> aClass, @NonNull String key) {
    return new TypeNoteKey<>(aClass, key);
  }
}
