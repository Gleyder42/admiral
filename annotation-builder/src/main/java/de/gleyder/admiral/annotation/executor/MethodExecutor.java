package de.gleyder.admiral.annotation.executor;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.executor.Executor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MethodExecutor implements Executor {

  private final ExecutableMethod method;

  @Override
  public void execute(@NonNull CommandContext<?> context) {
    method.invokeVoid(List.of(context.getSource()), ArgumentSupplier.ofBag(context.getBag()));
  }
}
