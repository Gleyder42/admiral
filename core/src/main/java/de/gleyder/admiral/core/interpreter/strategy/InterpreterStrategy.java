package de.gleyder.admiral.core.interpreter.strategy;

import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface InterpreterStrategy {

  List<InterpreterResult<Object>> test(@NonNull Map<String, Object> map, @NonNull Interpreter<?> interpreter,
                                       @NonNull InputArgument inputArgument);
}
