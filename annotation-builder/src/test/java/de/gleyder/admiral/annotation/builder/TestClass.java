package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.core.ValueBag;
import de.gleyder.admiral.core.error.LiteralCommandError;
import de.gleyder.admiral.core.error.ThrowableCommandError;
import de.gleyder.admiral.core.executor.CheckResult;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import de.gleyder.admiral.core.parser.InputArgument;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Node(value = "test", executor = "rootExecutor", aliases = {"t"})
public class TestClass {

  private static final String SUM_STRATEGY_INTERPRETER = "sumStrategyInterpreter";
  public static final int MID_EXECUTOR_RESULT = 100;
  public static final String MID = "mid";

  @Getter
  private final List<String> stringList = new ArrayList<>();

  //test calculate sum (10 10)
  @Route({
          @Node(value = "calculate", check = "verifier"),
          @Node(value = "sum", executor = "midExecutor"),
          @Node(value = "input", strategy = SUM_STRATEGY_INTERPRETER, interpreter = "long")
  })
  public void executor(Object source, @Bag("input") long sum) {
    stringList.add("sum:" + sum);
  }

  @Route({
          @Node("many"), @Node("values"),
          @Node(value = "string", interpreter = "string"),
          @Node(value = "int", interpreter = "int"),
          @Node(value = "char", interpreter = "char")
  })
  public void manyValues(Object source, @Bag("char") char character, @Bag("int") int integer,
                         @Bag("string") String string, @Bag("self") ValueBag bag) {
    stringList.add(character + "");
    stringList.add(integer + "");
    stringList.add(string + "");
  }

  @Route({
          @Node("flemming")
  })
  public void noValue(Object source, @Bag("hot") IntStream intStream) {
    //NullPointer because no value was found for IntStream
  }

  @Route({
          @Node(value = "bag", executor = "midExecutor", aliases = "b"),
          @Node(value = "supply", aliases = {"sup", "s"})
  })
  public void bagTest(Object source, @Bag(MID) int number) {
    stringList.add(number + "");
  }

  @ExecutorNode
  public void midExecutor(Object source, @Bag("self") ValueBag bag) {
    bag.add(MID, MID_EXECUTOR_RESULT);

    stringList.add(MID);
  }

  @CheckNode
  public CheckResult verifier(Object source) {
    stringList.add("verifier");
    return CheckResult.ofSuccessful();
  }

  @Route({
      @Node(value = "multipleCheckTest", check = {"trueCheck", "otherTrueCheck"})
  })
  public void multipleCheckTest(Object source) {
    stringList.add("multipleCheckTest");
  }

  @Route({
      @Node(value = "falseMultipleCheckTest", check = {"trueCheck", "otherTrueCheck", "falseCheck", "otherFalseCheck"})
  })
  public void falseMultipleCheckTest(Object source) {
    stringList.add("falseMultipleCheckTest");
  }

  @CheckNode
  public CheckResult trueCheck(Object source) {
    stringList.add("trueCheck");
    return CheckResult.ofSuccessful();
  }

  @CheckNode
  public CheckResult otherTrueCheck(Object source) {
    stringList.add("otherTrueCheck");
    return CheckResult.ofSuccessful();
  }

  @CheckNode
  public CheckResult falseCheck(Object source) {
    stringList.add("falseCheck");
    return CheckResult.ofError(LiteralCommandError.create().setMessage("False"));
  }

  @CheckNode
  public CheckResult otherFalseCheck(Object source) {
    stringList.add("otherFalseCheck");
    return CheckResult.ofError(LiteralCommandError.create().setMessage("Other False"));
  }

  public InterpreterResult<?> interpreter(String argument) {
    try {
      return InterpreterResult.ofValue(Long.parseLong(argument));
    } catch (NumberFormatException exception) {
      return InterpreterResult.ofError(new ThrowableCommandError(exception));
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

  @ExecutorNode
  public void rootExecutor(Object source) {
    stringList.add("root");
  }
}
