package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class ExecutableMethod {

  private final MethodCaller methodCaller;
  private final Parameter[] parameters;

  @SneakyThrows
  public ExecutableMethod(Object instance, Method method) {
    parameters = method.getParameters();
    Implementation.Composable composable = MethodCall
        .invoke(method)
        .on(instance)
        .withArgumentArrayElements(0, parameters.length)
        .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

    methodCaller = new ByteBuddy().subclass(MethodCaller.class)
        .method(ElementMatchers.named("call"))
        .intercept(composable)
        .make()
        .load(getClass().getClassLoader())
        .getLoaded()
        .getConstructor()
        .newInstance();
  }

  @SneakyThrows
  private Object invoke(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    List<Object> objects = supplier.toMethodArguments(preArguments, parameters);
    return methodCaller.call(objects.toArray());
  }

  public void invokeVoid(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    invoke(preArguments, supplier);
  }

  @SneakyThrows
  public <T> T invokeReturn(@NonNull List<Object> preArguments, @NonNull ArgumentSupplier supplier) {
    return (T) invoke(preArguments, supplier);
  }

  public interface MethodCaller {

    Object call(Object[] array);
  }
}
