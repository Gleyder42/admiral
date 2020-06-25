package de.gleyder.admiral.core;

import lombok.NonNull;

public class CommandDispatcherException extends RuntimeException {

  public CommandDispatcherException(@NonNull String message) {
    super(message);
  }
}
