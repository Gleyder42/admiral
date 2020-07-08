package de.gleyder.admiral.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains commonly used methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdmiralCommon {

  public static <T> T standard(@Nullable T value, @NonNull T standardValue) {
    return value == null ? standardValue : value;
  }
}
