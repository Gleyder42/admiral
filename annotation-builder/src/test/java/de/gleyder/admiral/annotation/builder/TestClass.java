package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.core.ValueBag;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Command("test")
public class TestClass {

  private final static String SUM_STRATEGY_INTERPRETER = "sumStrategyInterpreter";

  @Getter
  private final List<String> stringList = new ArrayList<>();

  //test calculate sum (10 10)
  @Route({
          @Node(value = "calculate", required = "verifier"),
          @Node(value = "sum", executor = "midExecutor"),
          @Node(value = "input", strategy = SUM_STRATEGY_INTERPRETER, interpreter = "long")
  })
  @ExecutorNode
  public void executor(Object source, ValueBag bag) {
    stringList.add("sum:" + bag.get("input").orElseThrow());
  }

  @ExecutorNode
  public void midExecutor(Object source, ValueBag bag) {
    stringList.add("mid");
  }

  @RequiredNode
  public boolean verifier(Object source, ValueBag bag) {
    stringList.add("verifier");
    return true;
  }

  public InterpreterResult<?> interpreter(String argument) {
    try {
      return InterpreterResult.ofValue(Long.parseLong(argument));
    } catch (NumberFormatException exception) {
      return InterpreterResult.ofError(exception);
    }
  }

  @InterpreterStrategyNode(SUM_STRATEGY_INTERPRETER)
  public List<InterpreterResult<Object>> strategy(Map<String, Object> map, Interpreter<?> interpreter, InputArgument inputArgument) {
    stringList.add("strategy");
    long sum = inputArgument.getInputs().stream()
            .filter(string -> interpreter(string).succeeded())
            .mapToLong(Long::parseLong)
            .sum();
    return List.of(InterpreterResult.ofValue(sum));
  }
}
