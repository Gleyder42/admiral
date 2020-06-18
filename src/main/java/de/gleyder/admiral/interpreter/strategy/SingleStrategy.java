package de.gleyder.admiral.interpreter.strategy;

import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.InterpreterResult;
import de.gleyder.admiral.parser.InputArgument;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleStrategy implements InterpreterStrategy {

  @Override
  public List<InterpreterResult<Object>> test(@NonNull Interpreter<?> interpreter, @NonNull InputArgument inputArgument, @NonNull Map<String, Object> map) {
    List<InterpreterResult<Object>> resultList = new ArrayList<>();
    //noinspection unchecked
    inputArgument.getInputs().forEach(argument -> resultList.add((InterpreterResult<Object>) interpreter.interpret(map, argument)));
    return resultList;
  }

}
