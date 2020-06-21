package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.core.ValueBag;
import de.gleyder.admiral.core.interpreter.InterpreterResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Command("test")
public class TestClass {

  @Getter
  private final List<String> stringList = new ArrayList<>();

  @Route({
          @Node(name = "item", required = "rn"),
          @Node(name = "verify"),
          @Node(name = "doubleBagKey", type = Double.class, interpreter = "doubleNodeKey")
  })
  @ExecutorNode("itemNode")
  public void itemNode(Object source, ValueBag bag) {
    stringList.add("node");
    stringList.add(bag.get("doubleBagKey").orElseThrow() + "");
  }

  @InterpreterNode("doubleNodeKey")
  public InterpreterResult<Double> doubleInterpreter(Map<String, Object> map, String argument) {
    try {
      return InterpreterResult.createSuccessful(Double.parseDouble(argument));
    } catch (NumberFormatException exception) {
      return InterpreterResult.createError(exception);
    }
  }

  @RequiredNode("rn")
  public boolean required(Object source, ValueBag bag) {
    stringList.add("rn");
    return true;
  }
}
