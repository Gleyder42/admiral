package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.core.CommandDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class AnnotationCommandBuilderTest {

  private final AnnotationCommandBuilder builder = new AnnotationCommandBuilder()
          .registerCommand(new TestClass());
  private final CommandDispatcher dispatcher = new CommandDispatcher();
  private final TestClass testClass = new TestClass();

  @BeforeEach
  void setup() {
    builder.registerCommand(testClass)
            .build(dispatcher);
  }

  @Test
  void containsItemNode() {
    dispatch("test item verify 5.5");

    assertContains(testClass, "rn", "node", "5.5");
  }

  void assertContains(TestClass testClass, String... strings) {
    assertIterableEquals(Arrays.asList(strings), testClass.getStringList());
  }

  void dispatch(String command) {
    dispatcher.dispatch(command, new Object(), new HashMap<>());
  }
}
