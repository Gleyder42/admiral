package de.gleyder.admiral.parser;

import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parsed the raw command arguments to a list of input arguments.
 *
 * @author Gleyder
 * @version 1.0
 * @since 1.0
 */
public class InputParser {

  private final char start = '(';
  private final char end = ')';
  private final char divide = ' ';

  public List<InputArgument> parse(@NonNull String command) {
    ArrayDeque<Character> collect = command.chars().mapToObj(c -> (char) c).collect(Collectors.toCollection(ArrayDeque::new));
    LinkedList<InputArgument> inputArguments = new LinkedList<>();
    parse(collect, inputArguments);
    return inputArguments;
  }

  private void parse(@NonNull Deque<Character> characterDeque, @NonNull LinkedList<InputArgument> argumentLinkedList) {
    StringBuilder builder = new StringBuilder();

    while (!characterDeque.isEmpty()) {
      Character currentChar = characterDeque.pop();

      if (currentChar != divide && currentChar != start && currentChar != end) {
        builder.append(currentChar);
      }

      if (currentChar == divide || characterDeque.isEmpty() || currentChar == end) {
        String input = builder.toString();
        if (!input.isEmpty()) {
          argumentLinkedList.add(new InputArgument(input));
        }
        builder = new StringBuilder();
      }

      if (currentChar == start) {
        LinkedList<InputArgument> insideArgumentList = new LinkedList<>();
        parse(characterDeque, insideArgumentList);

        InputArgument inputArgument = new InputArgument();
        insideArgumentList.forEach(inside -> inputArgument.getInputs().add(inside.getMerged()));
        argumentLinkedList.addLast(inputArgument);
      } else if (currentChar == end) {
        return;
      }
    }
  }
}
