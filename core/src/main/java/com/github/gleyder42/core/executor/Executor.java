package com.github.gleyder42.core.executor;

import com.github.gleyder42.core.CommandContext;
import lombok.NonNull;

public interface Executor {

  void execute(@NonNull CommandContext context);
}
