package de.gleyder.admiral;

import lombok.NonNull;

public interface Executor {

  void execute(@NonNull CommandContext<?> context);
}
