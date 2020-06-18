package de.gleyder.admiral;

import de.gleyder.admiral.parser.InputArgument;
import de.gleyder.admiral.parser.InputParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Admiral {

  public static void main(String[] args) {
    InputParser parser = new InputParser();
    List<InputArgument> testWelt = parser.parse("yalla was geht ab du sack?");

    System.out.println(testWelt);
  }

  private static List<InputArgument> singleLineArguments(String... stringArray) {
    return Arrays.stream(stringArray)
        .map(string -> {
          InputArgument inputArgument = new InputArgument();
          inputArgument.getInputs().add(string);
          return inputArgument;
        })
        .collect(Collectors.toUnmodifiableList());
  }
}
