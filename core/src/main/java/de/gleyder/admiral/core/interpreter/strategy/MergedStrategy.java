package de.gleyder.admiral.core.interpreter.strategy;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class MergedStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument) {
    //noinspection unchecked
    return List.of( (InterpreterResult<Object>) interpreter.interpret(map, inputArgument.getMerged()));
  }
}
