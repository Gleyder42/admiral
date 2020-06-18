package de.gleyder.admiral.interpreter;

import lombok.NonNull;

import java.util.Map;

public interface Interpreter<T> {

  InterpreterResult<T> interpret(@NonNull Map<String, Object> map, @NonNull String argument);
}
