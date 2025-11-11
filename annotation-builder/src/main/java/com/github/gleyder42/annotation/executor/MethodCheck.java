package com.github.gleyder42.annotation.executor;

import com.github.gleyder42.annotation.supplier.ArgumentSupplier;
import com.github.gleyder42.core.CommandContext;
import com.github.gleyder42.core.error.ThrowableCommandError;
import com.github.gleyder42.core.executor.Check;
import com.github.gleyder42.core.executor.CheckResult;
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
              method.invokeReturn(List.of(context.<Object>getSource()), ArgumentSupplier.ofBag(context.getBag()));
      if (result.wasSuccessful()) {
        return CheckResult.ofSuccessful();
      } else {
        return result;
      }
    } catch (Exception exception) {
      return CheckResult.ofError(new ThrowableCommandError(exception));
    }
  }
}
