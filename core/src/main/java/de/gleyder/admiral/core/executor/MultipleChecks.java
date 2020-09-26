package de.gleyder.admiral.core.executor;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.error.MultipleCommandError;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MultipleChecks implements Check {

  private final Collection<Check> checks;
  private final Type type;

  @Override
  public CheckResult test(@NonNull CommandContext context) {
    var results = checks.stream()
        .map(check -> check.test(context))
        .collect(Collectors.toList());
    return type.getFunction().apply(results, checks);
  }

  public enum Type {
    ALL((results, all) -> {
      var failed = getFailed(results);
      return failed.isEmpty() ? CheckResult.ofSuccessful() : toError(failed);
    }),
    ANY((results, all) -> {
      var failed = getFailed(results);
      return failed.size() != all.size() ? CheckResult.ofSuccessful() : toError(failed);
    }),
    NONE((results, all) -> {
      var failed = getFailed(results);
      return failed.size() == all.size() ? CheckResult.ofSuccessful() : toError(failed);
    });

    @Getter
    private final BiFunction<List<CheckResult>, Collection<Check>, CheckResult> function;

    Type(@NonNull BiFunction<List<CheckResult>, Collection<Check>, CheckResult> function) {
      this.function = function;
    }

    private static List<CheckResult> getFailed(List<CheckResult> results) {
      return results.stream()
          .filter(checkResult -> !checkResult.wasSuccessful()).collect(Collectors.toList());
    }

    private static CheckResult toError(@NonNull List<CheckResult> results) {
      return CheckResult.ofError(new MultipleCommandError(results.stream()
          .map(checkResult -> checkResult.getError().orElseThrow())
          .collect(Collectors.toList())
      ));
    }
  }
}
