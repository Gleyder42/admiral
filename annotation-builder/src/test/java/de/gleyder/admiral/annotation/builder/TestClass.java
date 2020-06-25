package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.core.ValueBag;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;

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
  public void executor(Object source, @Bag("input") long sum) {
    stringList.add("sum:" + sum);
  }

  @Route({
          @Node("many"), @Node("values"),
          @Node(value = "string", interpreter = "string"),
          @Node(value = "int", interpreter = "int"),
          @Node(value = "char", interpreter = "char")
  })
  @ExecutorNode
  public void manyValues(Object source, @Bag("char") char character, @Bag("int") int integer,
                         @Bag("string") String string) {
    stringList.add(character + "");
    stringList.add(integer + "");
    stringList.add(string + "");
  }

  @Route({
          @Node("flemming")
  })
  @ExecutorNode
  public void noValue(Object source, @Bag("hot") IntStream intStream) {
    //Error
  }

  @ExecutorNode
  public void midExecutor(Object source) {
    stringList.add("mid");
  }

  @RequiredNode
  public boolean verifier(Object source) {
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
  public List<InterpreterResult<Object>> strategy(Interpreter<?> interpreter, InputArgument inputArgument) {
    stringList.add("strategy");
    long sum = inputArgument.getInputs().stream()
            .filter(string -> interpreter(string).succeeded())
            .mapToLong(Long::parseLong)
            .sum();
    return List.of(InterpreterResult.ofValue(sum));
  }
}
