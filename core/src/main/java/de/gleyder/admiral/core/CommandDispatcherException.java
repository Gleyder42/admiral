package de.gleyder.admiral.core;

import lombok.NonNull;

import java.util.List;

public class CommandDispatcherException extends RuntimeException {

  public CommandDispatcherException(@NonNull String message) {
    super(message);
  }
}
