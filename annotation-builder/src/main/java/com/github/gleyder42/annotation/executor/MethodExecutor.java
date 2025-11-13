package com.github.gleyder42.annotation.executor;

import com.github.gleyder42.annotation.supplier.ArgumentSupplier;
import com.github.gleyder42.core.CommandContext;
import com.github.gleyder42.core.executor.Executor;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MethodExecutor implements Executor {

  private final ExecutableMethod method;

  @Override
  public void execute(@NonNull CommandContext context) {
    method.invokeVoid(List.of(context.source()), ArgumentSupplier.ofBag(context.bag()));
  }
}
