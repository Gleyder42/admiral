package de.gleyder.admiral.core.parser;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses the raw command arguments to a list of input arguments.
 */
public class InputParser {

  private final char start;
  private final char end;
  private final char divider;

  public InputParser(@Nullable Character start, @Nullable Character end, @Nullable Character divider) {
    this.start = Objects.requireNonNullElse(start, '(');
    this.end = Objects.requireNonNullElse(end, ')');
    this.divider = Objects.requireNonNullElse(divider, ' ');
  }

  public InputParser() {
    this(null, null, null);
  }

  public List<InputArgument> parse(@NonNull String command) {
    ArrayDeque<Character> collect = command.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toCollection(ArrayDeque::new));
    LinkedList<InputArgument> inputArguments = new LinkedList<>();
    parse(collect, inputArguments);
    return inputArguments;
  }

  private void parse(@NonNull Deque<Character> characterDeque, @NonNull LinkedList<InputArgument> argumentLinkedList) {
    StringBuilder builder = new StringBuilder();

    while (!characterDeque.isEmpty()) {
      Character currentChar = characterDeque.pop();

      if (currentChar != divider && currentChar != start && currentChar != end) {
        builder.append(currentChar);
      }

      if (currentChar == divider || characterDeque.isEmpty() || currentChar == end) {
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
