package de.gleyder.admiral.interpreter.strategy;

import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.InterpreterResult;
import de.gleyder.admiral.parser.InputArgument;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class MergedStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument, @NonNull Map<String, Object> map) {
    //noinspection unchecked
    return List.of( (InterpreterResult<Object>) interpreter.interpret(map, inputArgument.getMerged()));
  }
}
