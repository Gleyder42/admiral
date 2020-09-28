package de.gleyder.admiral.core;

import lombok.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Stores key value entries. One key can hold multiple values.
 * This class is like a {@see Map}, except that a values are list, or a Multimap from Google Guava
 */
@EqualsAndHashCode
@ToString
public class ValueBag {

  private final Map<String, List<Object>> map = new HashMap<>();

  /**
   * Adds another bag to this bag.
   *
   * @param bag   another bag
   */
  public void addBag(@NonNull ValueBag bag) {
    map.putAll(bag.map);
  }

  /**
   * Adds a key value pair.
   *
   * @param key       the key
   * @param object    the value
   */
  public void add(@NonNull String key, @NonNull Object object) {
    getValues(key).add(object);
  }

  /**
   * Checks if the provided key holds multiple values.
   *
   * @param key     the key
   * @return        if the key holds multiple values
   */
  public boolean isMulti(@NonNull String key) {
    if (isAbsent(key)) {
      return false;
    }
    return map.get(key).size() > 1;
  }

  /**
   * Checks if the ValueBag holds a value to the provided key.
   *
   * @param key     the key
   * @return        if the ValueBag holds a value to the provided key
   */
  public boolean contains(@NonNull String key) {
    if (isAbsent(key)) {
      return false;
    }

    return !map.get(key).isEmpty();
  }

  /**
   * Returns only one value from the provided key.
   *
   * @param key     the key
   * @param clazz   the class to which the optional should be casted
   * @return        the value associated with the provided key
   */
  public <T> Optional<T> get(@NonNull String key, @NonNull Class<T> clazz) {
    return get(key);
  }

  /**
   * Returns only one value from the provided key.
   *
   * @param key     the key
   * @return        the value associated with the provided key
   * @throws        NullPointerException if more than one value is associated with the provided key
   */
  public <T> Optional<T> get(@NonNull String key) {
    if (isAbsent(key)) {
      return Optional.empty();
    }
    List<Object> loadedMap = getValues(key);
    if (loadedMap.size() > 1) {
      throw new IllegalStateException("More than one value is associated with '" + key + "'");
    }
    return Optional.ofNullable((T) loadedMap.get(0));
  }

  /**
   * Returns a list of values from the provided key.
   *
   * @param key     the key
   * @return        a list of values from the provided key.
   *                If no values are found, an empty list is returned
   */
  public <T> List<T> getList(@NonNull String key) {
    if (isAbsent(key)) {
      return Collections.emptyList();
    }
    return (List<T>) List.copyOf(getValues(key));
  }

  /**
   * Returns all values which are stored.
   *
   * @return      all values which are stored
   */
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

  /**
   * Returns all keys.
   *
   * @return      all keys
   */
  public Set<String> keySet() {
    return map.keySet();
  }

  /**
   * Loops through every entry.
   *
   * @param consumer      a BiConsumer
   */
  public void forEach(@NonNull BiConsumer<String, List<Object>> consumer) {
    map.forEach(consumer);
  }

  /**
   * Returns a set of all entries.
   *
   * @return      set of all entries
   */
  public Set<Map.Entry<String, List<Object>>> entrySet() {
    return map.entrySet();
  }

  /**
   * Returns a set of all entries.
   * A value is a list, of multiple values are found.
   * If only one value is found, the entry has the value directly.
   *
   * <p></p>
   * (key, [hello]) -> (key, hello)
   * (key, [hello, world]) -> (key, [hello world])
   *
   * @return    a set of all entries
   */
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

  private boolean isAbsent(@NonNull String key) {
    return !map.containsKey(key) || map.get(key).isEmpty();
  }

  private List<Object> getValues(String key) {
    return map.computeIfAbsent(key, ignored -> new ArrayList<>());
  }

  @Getter
  private static class SimpleEntry implements Map.Entry<String, Object> {

    private final String key;
    private Object value;

    public SimpleEntry(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public Object setValue(Object value) {
      this.value = value;
      return this.value;
    }
  }
}
