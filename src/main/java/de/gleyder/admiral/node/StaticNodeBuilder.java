package de.gleyder.admiral.node;

import lombok.NonNull;

/**
 * @author Eric
 * @version 18.06.2020
 */
public class StaticNodeBuilder extends CommandNodeBuilder<StaticNodeBuilder, StaticNode> {

  public StaticNodeBuilder(@NonNull String key) {
    super(new StaticNode(key));
  }

}
