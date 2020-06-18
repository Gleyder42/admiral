package de.gleyder.admiral.builder;

import de.gleyder.admiral.node.StaticNode;
import lombok.NonNull;

public class StaticNodeBuilder extends CommandNodeBuilder<StaticNodeBuilder, StaticNode> {

  public StaticNodeBuilder(@NonNull String key) {
    super(new StaticNode(key));
  }
}
