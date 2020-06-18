package de.gleyder.admiral.node;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class TypeNoteKey<T> implements NodeKey {

  @Getter
  private final String typeName;

  @Getter
  private final String name;

  public TypeNoteKey(@NonNull Class<T> aClass, @NonNull String name) {
    this.typeName = aClass.getSimpleName();
    this.name = name;
  }

  @Override
  public String get() {
    return name;
  }
}
