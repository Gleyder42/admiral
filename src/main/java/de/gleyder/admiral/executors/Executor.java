package de.gleyder.admiral.executors;

import de.gleyder.admiral.CommandContext;
import lombok.NonNull;

public interface Executor {

  void execute(@NonNull CommandContext<?> context);
}
