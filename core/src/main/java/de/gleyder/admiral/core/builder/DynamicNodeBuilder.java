package de.gleyder.admiral.core.builder;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import lombok.NonNull;

public class DynamicNodeBuilder extends CommandNodeBuilder<DynamicNodeBuilder, DynamicNode> {

  public DynamicNodeBuilder(@NonNull String key) {
    super(new DynamicNode(key));
  }

  public DynamicNodeBuilder setInterpreterStrategy(@NonNull InterpreterStrategy strategy) {
    node.setInterpreterStrategy(strategy);
    return this;
  }

  public DynamicNodeBuilder setInterpreter(@NonNull Interpreter<?> interpreter) {
    node.setInterpreter(interpreter);
    return this;
  }
}
