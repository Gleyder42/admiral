package de.gleyder.admiral;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ValueBag {

  private final Map<String, List<Object>> map = new HashMap<>();

  public void add(@NonNull String key, @NonNull Object object) {
    getMap(key).add(object);
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(@NonNull String key) {
    if (!map.containsKey(key)) {
      return Optional.empty();
    }
    List<Object> loadedMap = getMap(key);
    if (loadedMap.size() > 1) {
      throw new IllegalStateException("More than one value is associated with '" + key + "'");
    }
    return Optional.ofNullable((T) loadedMap.get(0));
  }

  public <T> Optional<List<T>> getList(@NonNull String key) {
    if (!map.containsKey(key)) {
      return Optional.empty();
    }
    //noinspection unchecked
    return Optional.of((List<T>) getMap(key));
  }

  private List<Object> getMap(String key) {
    return map.computeIfAbsent(key, ignored -> new ArrayList<>());
  }
}
