package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import de.gleyder.admiral.core.CommandContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class MethodRequired implements Predicate<CommandContext> {

  private final ExecutableMethod method;

  @Override
  public boolean test(CommandContext context) {
    return method.invokeReturn(List.of(context.getSource(Object.class)), ArgumentSupplier.ofBag(context.getBag()));
  }
}
