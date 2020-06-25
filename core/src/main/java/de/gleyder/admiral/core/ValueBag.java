package de.gleyder.admiral.core;

import lombok.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@ToString
public class ValueBag {

  private final Map<String, List<Object>> map = new HashMap<>();

  public void add(@NonNull String key, @NonNull Object object) {
    getMap(key).add(object);
  }

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
    return Optional.of((List<T>) getMap(key));
  }

  public List<Object> getAll() {
    List<Object> objectList = new ArrayList<>();
    map.forEach((key, value) -> {
      if (value.isEmpty()) {
        return;
      }
      if (value.size() == 1) {
        objectList.add(value.get(0));
      } else {
        objectList.add(value);
      }
    });
    return objectList;
  }

  private List<Object> getMap(String key) {
    return map.computeIfAbsent(key, ignored -> new ArrayList<>());
  }

  public Set<String> keySet() {
    return map.keySet();
  }

  public void forEach(@NonNull BiConsumer<String, List<Object>> consumer) {
    map.forEach(consumer);
  }

  public Set<Map.Entry<String, List<Object>>> entrySet() {
    return map.entrySet();
  }

  public Set<Map.Entry<String, Object>> objectEntrySet() {
    return map.entrySet().stream()
            .map(entry -> {
              if (entry.getValue().size() == 1) {
                return new SimpleEntry(entry.getKey(), entry.getValue().get(0));
              } else {
                return new SimpleEntry(entry.getKey(), entry.getValue());
              }
            })
            .collect(Collectors.toSet());
  }

  private static class SimpleEntry implements Map.Entry<String, Object> {

    private final String key;
    private Object value;

    public SimpleEntry(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public Object setValue(Object value) {
      return this.value = value;
    }
  }
}
