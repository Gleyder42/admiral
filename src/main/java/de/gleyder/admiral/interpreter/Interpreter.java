package de.gleyder.admiral.interpreter;

import lombok.NonNull;

public interface Interpreter<T> {

  InterpreterResult<T> interpret(@NonNull String argument);
}
