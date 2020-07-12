package de.gleyder.admiral.core.interpreter;

import de.gleyder.admiral.core.error.LiteralCommandError;
import lombok.NonNull;

public interface NumberInterpreter<N extends Number> extends SimpleInterpreter<N> {

  N parse(@NonNull String argument);

  @Override
  default InterpreterResult<N> interpret(@NonNull String argument) {
    try {
      return InterpreterResult.ofValue(parse(argument));
    } catch (NumberFormatException exception) {
      return InterpreterResult.ofError(LiteralCommandError.create().setMessage(exception.toString()));
    }
  }

}
