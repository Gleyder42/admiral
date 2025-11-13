package com.github.gleyder42.annotation.supplier;

import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class MapArgumentSupplier implements ArgumentSupplier {

  @NonNull
  private final Map<String, Object> map;

  @Override
  public @Nullable Object get(@NonNull String key) {
    return map.get(key);
  }
}
