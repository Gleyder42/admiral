package de.gleyder.admiral.core.node;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.StringInterpreter;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * A dynamic node is the point, where the user can input data.
 */
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
}
