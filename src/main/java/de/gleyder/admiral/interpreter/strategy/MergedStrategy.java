package de.gleyder.admiral.interpreter.strategy;

import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.InterpreterResult;
import de.gleyder.admiral.parser.InputArgument;
import lombok.NonNull;

import java.util.List;

public class MergedStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument) {
    @SuppressWarnings("unchecked") InterpreterResult<Object> result =
        (InterpreterResult<Object>) interpreter.interpret(inputArgument.getMerged());
    return List.of(result);
  }
}
