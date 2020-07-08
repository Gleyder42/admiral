package de.gleyder.admiral.core.node;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Static nodes defines the command structure.
 */
public class StaticNode extends CommandNode {

  @Getter
  private final List<String> aliases = new ArrayList<>();

  public StaticNode(@NonNull String key) {
    super(key);
  }
}
