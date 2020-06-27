package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import de.gleyder.admiral.core.CommandContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class MethodRequired implements Predicate<CommandContext<? super Object>> {

  private final ExecutableMethod method;

  @Override
  public boolean test(CommandContext<? super Object> context) {
    return method.invokeReturn(List.of(context.getSource()), ArgumentSupplier.ofBag(context.getBag()));
  }
}
