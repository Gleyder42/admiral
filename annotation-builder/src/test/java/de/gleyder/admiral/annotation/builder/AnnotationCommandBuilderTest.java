package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.core.CommandDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationCommandBuilderTest {

  private final TestClass testClass = new TestClass();
  private final AnnotationCommandBuilder builder = new AnnotationCommandBuilder()
          .registerCommand(testClass);
  private final CommandDispatcher dispatcher = new CommandDispatcher();

  @BeforeEach
  void setup() {
    builder.registerCommand(testClass)
            .build(dispatcher);
  }

  @Test
  void calculateCommand() {
    dispatch("test calculate sum (10 10)");

    assertContains(testClass, "strategy", "verifier", "mid", "strategy", "sum:20");
  }

  @Test
  void manyValuesCommand() {
    dispatch("test many values hallo 10 c");

    assertContains(testClass, "c", "10", "hallo");
  }

  @Test
  void noValue() {
    assertThrows(NullPointerException.class, () -> dispatch("test flemming"));
  }

  @Test
  public void extraBagValue() {
    dispatch("test bag supply");

    assertContains(testClass, "mid", "100");
  }

  void assertContains(TestClass testClass, String... strings) {
    assertIterableEquals(Arrays.asList(strings), testClass.getStringList());
  }

  void dispatch(String command) {
    dispatcher.dispatch(command, new Object(), new HashMap<>());
  }
}
