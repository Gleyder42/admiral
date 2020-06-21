package de.gleyder.admiral.core.node.key;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TypeNodeKey<T> implements NodeKey {

  @Getter
  private final String typeName;

  @Getter
  private final String name;

  public TypeNodeKey(@NonNull Class<T> aClass, @NonNull String name) {
    this.typeName = aClass.getSimpleName();
    this.name = name;
  }

  @Override
  public String get() {
    return name;
  }
}
