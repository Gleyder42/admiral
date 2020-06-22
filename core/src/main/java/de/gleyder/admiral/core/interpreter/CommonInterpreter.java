package de.gleyder.admiral.core.interpreter;

public enum CommonInterpreter {

  BYTE(new ByteInterpreter()),
  SHORT(new ShortInterpreter()),
  INT(new IntegerInterpreter()),
  LONG(new LongInterpreter()),
  FLOAT(new FloatInterpreter()),
  DOUBLE(new DoubleInterpreter()),
  CHARACTER(new CharacterInterpreter()),
  STRING(new StringInterpreter()),
  BOOLEAN(new BooleanInterpreter())
  ;

  private Interpreter<Object> interpreter;

  CommonInterpreter(Interpreter<?> interpreter) {
    //noinspection unchecked
    this.interpreter = (Interpreter<Object>) interpreter;
  }

  public Interpreter<Object> get() {
    return interpreter;
  }
}
