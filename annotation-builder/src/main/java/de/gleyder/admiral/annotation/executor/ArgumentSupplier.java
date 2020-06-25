package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.Bag;
import de.gleyder.admiral.core.ValueBag;
import lombok.NonNull;

import java.lang.reflect.Parameter;
import java.util.*;

public class ArgumentSupplier {

  private final Map<String, Object> map;

  public static ArgumentSupplier ofMap(@NonNull Map<String, Object> objectMap) {
    return new ArgumentSupplier(objectMap.entrySet());
  }

  public static ArgumentSupplier ofBag(@NonNull ValueBag bag) {
    return new ArgumentSupplier(bag.objectEntrySet());
  }

  public ArgumentSupplier(Set<Map.Entry<String, Object>> entrySet) {
    this.map = new HashMap<>();
    entrySet.forEach(stringObjectEntry -> map.put(stringObjectEntry.getKey(), stringObjectEntry.getValue()));
  }

  public List<Object> toObjectArgs(@NonNull List<Object> preArguments, @NonNull Parameter[] parameters) {
    List<Object> objectsList = new ArrayList<>(preArguments);
    Arrays.stream(parameters)
            .filter(parameter -> parameter.isAnnotationPresent(Bag.class))
            .map(parameter -> parameter.getAnnotation(Bag.class))
            .map(bag -> {
              Object obj = map.get(bag.value());
              if (obj == null && !bag.nullable()) {
                throw new NullPointerException("No value for key " + bag.value());
              }
              return obj;
            })
            .forEach(objectsList::add);
    return objectsList;
  }
}
