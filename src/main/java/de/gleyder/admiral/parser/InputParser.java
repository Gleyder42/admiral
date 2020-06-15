package de.gleyder.admiral.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parsed the raw command arguments to a list of input arguments.
 *
 * @author Gleyder
 * @version 1.0
 * @since 1.0
 */
public class InputParser {

  @Getter
  private final char prefix;
  private final char suffix;
  private final char excluder;
  private final char spliter;

  /**
   * Creates an InputParser
   */
  public InputParser() {
    this('(', ')', '\\', ' ');
  }

  /**
   * Creates an InputParser
   *
   * @param prefix   a prefix
   * @param suffix   a suffix
   * @param excluder a exclude
   * @param spliter  a spliter
   */
  public InputParser(char prefix, char suffix, char excluder, char spliter) {
    this.check(prefix, suffix, excluder, spliter);
    this.spliter = spliter;
    this.prefix = prefix;
    this.suffix = suffix;
    this.excluder = excluder;
  }

  /**
   * Parses an array of arguments as endless.
   *
   * @param commandArguments an array of raw arguments
   * @param lastNormalIndex  the last normal index
   * @return an input container
   */
  public InputArgumentContainer parseCommand(String[] commandArguments, int lastNormalIndex) {
    return new InputArgumentContainer(parseCommand(commandArguments), parseEndless(commandArguments, lastNormalIndex));
  }

  /**
   * Parses an array of arguments normal
   *
   * @param commandArguments an array of raw arguments
   * @return a list of input arguments
   */
  public List<InputArgument> parseCommand(String[] commandArguments) {
    return this.parseCommand(toCharArray(commandArguments));
  }

  /**
   * Parses an array of raw command arguments as endless
   *
   * @param commandArguments an array of raw command arguments
   * @param lastNormalIndex  the last normal index
   * @return a list of input arguments
   */
  public List<InputArgument> parseEndless(String[] commandArguments, int lastNormalIndex) {
    if (lastNormalIndex + 1 > commandArguments.length) {
      return this.parseCommand(commandArguments);
    }

    String[] normalArgumentArray = Arrays.copyOfRange(commandArguments, 0, lastNormalIndex + 1);
    String[] endlessArgumentArray = Arrays.copyOfRange(commandArguments, lastNormalIndex + 1, commandArguments.length);

    List<InputArgument> inputArgumentList = this.parseCommand(normalArgumentArray);

    InputArgument endlessArgument = new InputArgument();
    for (String argument : endlessArgumentArray) {
      endlessArgument.getInputs().add(argument);
    }
    inputArgumentList.add(endlessArgument);

    return inputArgumentList;
  }

  /**
   * Parses a an array of raw arguments
   *
   * @param charArray an array of characters
   * @return a list of input arguments
   */
  private List<InputArgument> parseCommand(Character[] charArray) {
    return this.parseCommand(charArray, 0).getInputArgumentList();
  }

  /**
   * Parse a command to a List of InputArguments
   *
   * @param charArray the command as char array
   * @param level     the level (needs to be zero to work at the start) for recursive purpose
   * @return a List of InputArguments
   */
  private InputContainer parseCommand(Character[] charArray, int level) {
    /*
     * Level indicates the recursive level
     * Used to detected how 'deep' the recursion is to properly build the InputArgument
     */
    level++;
    StringBuilder builder = new StringBuilder();
    List<InputArgument> inputArgumentList = new ArrayList<>();

    for (int i = 0; i < charArray.length; i++) {
      char character = charArray[i];
      char preChar = i == 0 ? charArray[0] : charArray[i - 1];
      boolean excluded = preChar == this.excluder;

      if (character == this.excluder) {
        continue;
      }

      if (character == this.prefix && !excluded) {
        if (i + 1 > charArray.length - 1) {
          continue;
        }

        InputContainer inputContainer = parseCommand(Arrays.copyOfRange(charArray, i + 1, charArray.length), level);

        if (level == 1) {
          /*
           * Level 1 means that only one PREFIX is opened and not 'deeper'
           * This is necessary as the InputArgument inputs from the container are put in one InputArgument
           */
          InputArgument newInput = new InputArgument();
          inputContainer.getInputArgumentList()
              .forEach(inputArgument -> newInput.getInputs().addAll(inputArgument.getInputs()));

          inputArgumentList.add(newInput);
        } else {
          /*
           * Every other level than one merges the previous inputs in one
           */
          InputArgument newInput = new InputArgument();
          StringBuilder stringBuilder = new StringBuilder();

          inputContainer.getInputArgumentList()
              .forEach(input -> input.getInputs().forEach(string -> stringBuilder.append(string).append(" ")));
          newInput.getInputs().add(stringBuilder.toString().length() != 0 ?
              stringBuilder.deleteCharAt(stringBuilder.toString().length() - 1).toString() : "");
          inputArgumentList.add(newInput);
        }

        /*
         * Jumps to another locations as the recursive call parsed already a number of chars.
         * OutIndex indicates where if finished.
         * i is added to because the start of the recursive call is not the same as the call before.
         */
        i = inputContainer.getOutIndex() + i + 1;
        continue;
      }

      if ((character == this.spliter || character == this.suffix || i == charArray.length - 1) && !excluded) {
        /*
         * Checks if the current char is the last and not the SUFFIX.
         * The SUFFIX should not be in the final List but any other
         * char should
         */
        if (i == charArray.length - 1 && character != this.suffix && character != ' ') {
          builder.append(character);
        }

        /*
         * Creates a new InputArgument and adds the value
         * from the StringBuilder only if the builder is not empty
         */
        if (!builder.toString().isEmpty()) {
          InputArgument inputArgument = new InputArgument();
          inputArgument.getInputs().add(builder.toString());
          inputArgumentList.add(inputArgument);
          builder = new StringBuilder();
        }

        /*
         * If the current char is the SUFFIX the method has finished
         */
        if (character == this.suffix) {
          return new InputContainer(inputArgumentList, i);
        }
        continue;
      }

      /*
       * Appends the characters. Even if its the last statement it's one of the most important.
       */
      builder.append(character);
    }

    return new InputContainer(inputArgumentList, charArray.length);
  }

  /**
   * Breaks down a string into chars but puts a space after
   * every element, except the lase
   *
   * @param rawCommandInput the command
   * @return an array of Characters
   */
  private Character[] toCharArray(String[] rawCommandInput) {
    if (rawCommandInput == null || rawCommandInput.length == 0) {
      return new Character[] {};
    }

    List<Character> characterList = new ArrayList<>();
    for (String argument : rawCommandInput) {
      for (char character : argument.toCharArray()) {
        characterList.add(character);
      }
      characterList.add(' ');
    }
    characterList.remove(characterList.size() - 1);

    return characterList.toArray(new Character[0]);
  }

  /**
   * Check if the inputted chars are valid
   *
   * @param chars an array of chars
   */
  private void check(char... chars) {
    for (char string : chars) {
      if (!String.valueOf(string).matches("\\W")) {
        throw new InputParserException(string +
            " doesn't match the valid pattern. Only anything other than a letter, digit or underscore is allowed");
      }
    }

    for (int i = 0; i < chars.length; i++) {
      for (int j = i + 1; j < chars.length; j++) {
        if (chars[i] == chars[j]) {
          throw new InputParserException("Every characters needs to be unique in the parser");
        }
      }
    }
  }

  @Getter
  @ToString
  @AllArgsConstructor
  private static class InputContainer {

    private final List<InputArgument> inputArgumentList;
    private final int outIndex;
  }

}
