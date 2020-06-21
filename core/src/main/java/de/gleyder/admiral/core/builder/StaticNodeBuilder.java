package de.gleyder.admiral.core.builder;

import de.gleyder.admiral.core.node.StaticNode;
import lombok.NonNull;

public class StaticNodeBuilder extends CommandNodeBuilder<StaticNodeBuilder, StaticNode> {

  public StaticNodeBuilder(@NonNull String key) {
    super(new StaticNode(key));
  }
}
