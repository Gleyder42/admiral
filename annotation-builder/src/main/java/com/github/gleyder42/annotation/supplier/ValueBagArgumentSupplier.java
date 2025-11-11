package com.github.gleyder42.annotation.supplier;

import com.github.gleyder42.core.ValueBag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValueBagArgumentSupplier implements ArgumentSupplier {

  private final ValueBag bag;

  @Override
  public Object selfSupply(@NonNull String key) {
    return key.equalsIgnoreCase("self") ? bag : null;
  }

  @Override
  public Object get(@NonNull String key) {
    return bag.isMulti(key) ? bag.getList(key) : bag.get(key).orElse(null);
  }
}
