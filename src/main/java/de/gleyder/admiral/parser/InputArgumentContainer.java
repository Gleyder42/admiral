package de.gleyder.admiral.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Container for the normal and endless parsed input arguments.
 *
 * @author Gleyder
 * @version 1.0
 * @since 1.0
 */
@Getter
@AllArgsConstructor
@ToString
public class InputArgumentContainer {

  private final List<InputArgument> normal;
  private final List<InputArgument> withEndless;
}
