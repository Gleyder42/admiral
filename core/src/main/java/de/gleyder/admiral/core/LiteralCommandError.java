package de.gleyder.admiral.core;

import lombok.*;

/**
 * Implementation of {@link CommandError} for the case in
 * which an error can be easily described via a string.
 */
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LiteralCommandError implements CommandError {

  private String simple;
  private String detailed;

  public static LiteralCommandError create() {
    return new LiteralCommandError();
  }

  public LiteralCommandError setMessage(@NonNull String message, @NonNull Object... objects) {
    this.simple = String.format(message, objects);
    this.detailed = this.simple;
    return this;
  }

  public LiteralCommandError setSimple(@NonNull String message, @NonNull Object... objects) {
    this.simple = String.format(message, objects);
    return this;
  }

  public LiteralCommandError setDetailed(@NonNull String message, @NonNull Object... objects) {
    this.detailed = String.format(message, objects);
    return this;
  }

}
