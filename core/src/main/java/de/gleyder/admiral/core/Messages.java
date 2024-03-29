package de.gleyder.admiral.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum Messages {

  NO_COMMAND_FOUND("No matching command could be found for the specified parameters", "command.no-command"),
  NO_EXECUTOR_ON_ROUTE("The route has no executor", "command.no-executor"),
  FURTHER_ARGUMENTS_REMAIN("Further arguments remain [%s] but command tree has finished with node %s",
      "command.arguments-remain"),
  MULTIPLE_COMMANDS_FOUND("Several commands were found that match the specified parameters",
      "command.multiple-commands");

  @Setter
  private String message;

  @Getter
  private final String path;

  public String get(Object... objects) {
    return String.format(message, objects);
  }
}
