package de.gleyder.admiral.annotation.supplier;

import de.gleyder.admiral.annotation.Bag;
import de.gleyder.admiral.core.ValueBag;
import lombok.NonNull;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface ArgumentSupplier {

  static ArgumentSupplier ofBag(@NonNull ValueBag bag) {
    return new ValueBagArgumentSupplier(bag);
  }

  static ArgumentSupplier ofMap(@NonNull Map<String, Object> map) {
    return new MapArgumentSupplier(map);
  }

  Object get(@NonNull String key);

  default Object selfSupply(@NonNull String key) {
    return null;
  }

  default List<Object> toMethodArguments(@NonNull List<Object> preArguments, @NonNull Parameter[] parameters) {
    List<Object> objectsList = new ArrayList<>(preArguments);
    Arrays.stream(parameters)
            .filter(parameter -> parameter.isAnnotationPresent(Bag.class))
            .map(parameter -> parameter.getAnnotation(Bag.class))
            .map(bag -> {
              String key = bag.value();
              Object self = selfSupply(key);
              if (self != null) {
                return self;
              }

              Object obj = get(key);
              if (obj == null && !bag.nullable()) {
                throw new NullPointerException("No value for key " + key);
              }
              return obj;
            })
            .forEach(objectsList::add);
    return objectsList;
  }
}
