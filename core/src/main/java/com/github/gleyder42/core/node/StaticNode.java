package com.github.gleyder42.core.node;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

/**
 * Static nodes defines the command structure.
 */
public final class StaticNode extends CommandNode {

  @Getter
  private final List<String> aliases = new ArrayList<>();

  public StaticNode(@NonNull String key) {
    super(key);
  }
}
