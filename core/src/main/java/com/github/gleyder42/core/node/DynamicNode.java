package com.github.gleyder42.core.node;

import com.github.gleyder42.core.interpreter.Interpreter;
import com.github.gleyder42.core.interpreter.StringInterpreter;
import com.github.gleyder42.core.interpreter.strategy.InterpreterStrategy;
import com.github.gleyder42.core.interpreter.strategy.MergedStrategy;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * A dynamic node is the point, where the user can input data.
 */
@Slf4j
public final class DynamicNode extends CommandNode {

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
