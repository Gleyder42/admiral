package de.gleyder.admiral.node;

import lombok.NonNull;

/**
 * @author Eric
 * @version 18.06.2020
 */
public class StaticNode extends CommandNode<StringNodeKey> {

  public StaticNode(@NonNull String string) {
    super(NodeKey.ofStatic(string));
  }

}
