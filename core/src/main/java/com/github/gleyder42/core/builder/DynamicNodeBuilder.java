package com.github.gleyder42.core.builder;

import com.github.gleyder42.core.interpreter.CommonInterpreter;
import com.github.gleyder42.core.interpreter.Interpreter;
import com.github.gleyder42.core.node.DynamicNode;
import com.github.gleyder42.core.interpreter.strategy.InterpreterStrategy;
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

  public DynamicNodeBuilder setInterpreter(@NonNull CommonInterpreter commonInterpreter) {
    node.setInterpreter(commonInterpreter.get());
    return this;
  }
}
