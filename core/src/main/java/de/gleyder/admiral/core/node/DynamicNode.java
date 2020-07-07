package de.gleyder.admiral.core.node;

import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.interpreter.StringInterpreter;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class DynamicNode extends CommandNode {

  @Setter
  @Getter
  private InterpreterStrategy interpreterStrategy = new MergedStrategy();

  @Setter
  @Getter
  private Interpreter<?> interpreter = new StringInterpreter();

  public DynamicNode(@NonNull String key) {
    super(key);
  }

  @Override
  public void onCommandProcess(@NonNull CommandContext context, @NonNull Map<String, Object> interpreterMap, @NonNull InputArgument inputArgument) {
    List<InterpreterResult<Object>> results = interpreterStrategy.test(interpreterMap, interpreter, inputArgument);

    results.forEach(result -> {
      if (result.succeeded()) {
        context.getBag().add(getKey(), result.getValue().orElseThrow());
      } else {
        result.getError().ifPresent(error -> log.info(error.toString()));
      }
    });
  }
}
