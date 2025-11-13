package com.github.gleyder42.core.interpreter;

import java.util.Map;
import lombok.NonNull;

public interface Interpreter<T> {

  InterpreterResult<T> interpret(@NonNull Map<String, Object> map, @NonNull String argument);
}
