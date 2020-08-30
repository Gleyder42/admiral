package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.core.CommandDispatcher;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationCommandBuilderTest {

  public static final String ROOT = "root";
  public static final String MID = "mid";
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

    assertContains(testClass, "strategy", "verifier", ROOT, MID, "sum:20");
  }

  @Test
  void manyValuesCommand() {
    dispatch("test many values hallo 10 c");

    assertContains(testClass, ROOT, "c", "10", "hallo");
  }

  @Test
  void noValue() {
    assertThrows(NullPointerException.class, () -> dispatch("test flemming"));
  }

  @Test
  void extraBagValueWithAlias() {
    dispatch("t b s");

    assertContains(testClass, ROOT, MID, "100");
  }

  @Test
  void extraBagValue() {
    dispatch("test bag supply");

    assertContains(testClass, ROOT, MID, "100");
  }

  @Nested
  class CommandValidation {

    @TestFactory
    List<DynamicTest> allCommandsShouldBeValid() {
      return dispatcher.getAllRoutes().stream()
          .map(route -> DynamicTest.dynamicTest(route.getNodeList().toString(), () -> assertTrue(route.isValid())))
          .collect(Collectors.toUnmodifiableList());
    }

  }

  void assertContains(TestClass testClass, String... strings) {
    assertIterableEquals(Arrays.asList(strings), testClass.getStringList());
  }

  void dispatch(String command) {
    dispatcher.dispatch(command, new Object(), new HashMap<>());
  }
}
