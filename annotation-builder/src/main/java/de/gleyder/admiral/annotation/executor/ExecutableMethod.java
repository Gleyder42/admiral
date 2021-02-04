package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class ExecutableMethod {

  private final Object instance;
  private final Method method;

  @SneakyThrows
  private Object invoke(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    List<Object> objects = supplier.toMethodArguments(preArguments, method.getParameters());

    try {
      return method.invoke(instance, objects.toArray());
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Method '" + method.getName() + "' in class '" + method.getDeclaringClass().getName()
          + "'" + " needs following parameters " + Arrays.toString(method.getParameters()) + " but got " + objects, exception);
    }
  }

  public void invokeVoid(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    invoke(preArguments, supplier);
  }

  @SneakyThrows
  public <T> T invokeReturn(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    return (T) invoke(preArguments, supplier);
  }
}
