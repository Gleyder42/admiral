package de.gleyder.admiral.core.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class InputParserTest {

  private final InputParser inputParser = new InputParser();

  @Test
  void shouldReturnOneArgument() {
    Assertions.assertIterableEquals(singleLineArguments("test"),
        parseCommand("test"));
  }

  @Test
  void shouldReturnThreeArguments() {
    Assertions.assertIterableEquals(singleLineArguments("hello", "world", "lol"),
        parseCommand("hello world lol"));
  }

  @Test
  void shouldReturnOneMultiArgument() {
    Assertions.assertIterableEquals(multiLineArguments(List.of("ok", "boomer")),
        parseCommand("(ok boomer)"));
  }

  @Test
  void shouldReturnThreeMultiArguments() {
    Assertions.assertIterableEquals(multiLineArguments(List.of("ok", "boomer"), List.of("hallo", "welt")),
        parseCommand("(ok boomer) (hallo welt)"));
  }

  @Test
  void shouldReturnOneMultiArgumentWithSpace() {
    Assertions.assertIterableEquals(singleLineArguments("ok test"),
        parseCommand("((ok test))"));
  }

  @Test
  void shouldReturnThreeMultiArgumentsWithSpaces() {
    Assertions.assertIterableEquals(List.of(singleInputArgument("ok boomer", "whats up", "super cool")),
        parseCommand("((ok boomer) (whats up) (super cool))"));
  }

  @Test
  void allCombinedTest()  {
    final List<InputArgument> actual = parseCommand("hello world (this is cool) ((now this) (is epic)) ok boys");
    List<InputArgument> expected = new ArrayList<>();
    expected.addAll(singleLineArguments("hello", "world"));
    expected.add(singleInputArgument("this", "is", "cool"));
    expected.add(singleInputArgument("now this", "is epic"));
    expected.addAll(singleLineArguments("ok", "boys"));

    Assertions.assertIterableEquals(expected, actual);
  }

  private List<InputArgument> parseCommand(String command) {
    return inputParser.parse(command);
  }

  @SafeVarargs
  private List<InputArgument> multiLineArguments(List<String>... stringList) {
    return Arrays.stream(stringList)
        .map(list -> {
          InputArgument inputArgument = new InputArgument();
          inputArgument.getInputs().addAll(list);
          return inputArgument;
        })
        .collect(Collectors.toUnmodifiableList());
  }

  private List<InputArgument> singleLineArguments(String... stringArray) {
    return Arrays.stream(stringArray)
        .map(string -> {
          InputArgument inputArgument = new InputArgument();
          inputArgument.getInputs().add(string);
          return inputArgument;
        })
        .collect(Collectors.toUnmodifiableList());
  }

  private InputArgument singleInputArgument(String... stingArray) {
    InputArgument inputArgument = new InputArgument();
    inputArgument.getInputs().addAll(Arrays.asList(stingArray));
    return inputArgument;
  }
}
