package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.annotation.supplier.ArgumentSupplier;
import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.error.LiteralCommandError;
import de.gleyder.admiral.core.executor.Check;
import de.gleyder.admiral.core.executor.CheckResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MethodCheck implements Check {

  private final ExecutableMethod method;

  @Override
  public CheckResult test(@NonNull CommandContext context) {
    try {
      CheckResult result =
              method.invokeReturn(List.of(context.getSource(Object.class)), ArgumentSupplier.ofBag(context.getBag()));
      if (result.wasSuccessful()) {
        return CheckResult.ofSuccessful();
      } else {
        return result;
      }
    } catch (Exception exception) {
      return CheckResult.ofError(LiteralCommandError.create().setMessage(exception.toString()));
    }
  }
}
