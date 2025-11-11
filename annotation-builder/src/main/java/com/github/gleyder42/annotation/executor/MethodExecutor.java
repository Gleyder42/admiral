package com.github.gleyder42.annotation.executor;

import com.github.gleyder42.annotation.supplier.ArgumentSupplier;
import com.github.gleyder42.core.CommandContext;
import com.github.gleyder42.core.executor.Executor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MethodExecutor implements Executor {

  private final ExecutableMethod method;

  @Override
  public void execute(@NonNull CommandContext context) {
    method.invokeVoid(List.of(context.<Object>getSource()), ArgumentSupplier.ofBag(context.getBag()));
  }
}
