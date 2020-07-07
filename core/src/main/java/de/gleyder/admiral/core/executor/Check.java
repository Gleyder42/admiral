package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.CommandContext;
import lombok.NonNull;

public interface Check {

  CheckResult test(@NonNull CommandContext commandContext);

}
