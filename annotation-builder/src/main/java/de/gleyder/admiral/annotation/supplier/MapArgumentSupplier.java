package de.gleyder.admiral.annotation.supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MapArgumentSupplier implements ArgumentSupplier {

  @NonNull
  private final Map<String, Object> map;

  @Override
  public Object get(@NonNull String key) {
    return map.get(key);
  }
}
