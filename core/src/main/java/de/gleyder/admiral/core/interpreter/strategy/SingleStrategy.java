package de.gleyder.admiral.core.interpreter.strategy;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter,
                                              @NonNull InputArgument inputArgument) {
    List<InterpreterResult<Object>> resultList = new ArrayList<>();
    inputArgument.getInputs()
        .forEach(argument -> resultList.add((InterpreterResult<Object>) interpreter.interpret(map, argument)));
    return resultList;
  }

}
