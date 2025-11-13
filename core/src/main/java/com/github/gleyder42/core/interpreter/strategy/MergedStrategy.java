package com.github.gleyder42.core.interpreter.strategy;

import com.github.gleyder42.core.interpreter.Interpreter;
import com.github.gleyder42.core.interpreter.InterpreterResult;
import com.github.gleyder42.core.parser.InputArgument;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class MergedStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter,
                                              @NonNull InputArgument inputArgument) {
    return List.of((InterpreterResult<Object>) interpreter.interpret(map, inputArgument.getMerged()));
  }
}
