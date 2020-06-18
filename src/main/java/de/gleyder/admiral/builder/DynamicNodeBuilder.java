package de.gleyder.admiral.builder;

import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.node.DynamicNode;
import lombok.NonNull;

public class DynamicNodeBuilder extends CommandNodeBuilder<DynamicNodeBuilder, DynamicNode> {

  public DynamicNodeBuilder(@NonNull Class<?> aClass, @NonNull String key) {
    super(new DynamicNode(aClass, key));
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
