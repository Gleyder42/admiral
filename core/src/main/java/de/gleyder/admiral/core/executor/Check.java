package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.error.MultipleCommandError;
import lombok.NonNull;

import java.util.Collection;
import java.util.stream.Collectors;

public interface Check {

  static Check ofMultiple(@NonNull Collection<Check> collection) {
    return context -> {
      var errorList = collection.stream()
          .map(check -> check.test(context))
          .filter(checkResult -> checkResult.getError().isPresent())
          .map(checkResult -> checkResult.getError().get())
          .collect(Collectors.toList());
      if (errorList.isEmpty()) {
        return CheckResult.ofSuccessful();
      }
      return CheckResult.ofError(new MultipleCommandError(errorList));
    };
  }

  CheckResult test(@NonNull CommandContext commandContext);
}
