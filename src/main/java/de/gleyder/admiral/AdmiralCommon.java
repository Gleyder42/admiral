package de.gleyder.admiral;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdmiralCommon {

  public static <T> T standard(@Nullable T value, @NonNull T newValue) {
    return value == null ? newValue : value;
  }
}
