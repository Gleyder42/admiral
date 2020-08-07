package de.gleyder.admiral.core.error;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class ThrowableCommandError implements CommandError {

  private final Throwable throwable;
  private final Function<Throwable, String> simpleMessage;
  private final Function<Throwable, String> detailedMessage;

  public ThrowableCommandError(@NonNull Throwable throwable, @Nullable Function<Throwable, String> simpleMessage,
                               @Nullable Function<Throwable, String> detailedMessage) {
    this.throwable = throwable;
    this.simpleMessage = Objects.requireNonNullElse(simpleMessage, Throwable::getMessage);
    this.detailedMessage = Objects.requireNonNullElse(detailedMessage, Throwable::getMessage);
  }

  public ThrowableCommandError(@NonNull Throwable throwable) {
    this(throwable, null, null);
  }

  @Override
  public String getSimple() {
    return simpleMessage.apply(throwable);
  }

  @Override
  public String getDetailed() {
    return detailedMessage.apply(throwable);
  }
}
