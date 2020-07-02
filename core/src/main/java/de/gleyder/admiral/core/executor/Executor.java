package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.CommandContext;
import lombok.NonNull;

public interface Executor {

  void execute(@NonNull CommandContext context);
}
