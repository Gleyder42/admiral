package de.gleyder.admiral.node;

import de.gleyder.admiral.node.key.NodeKey;
import de.gleyder.admiral.node.key.StringNodeKey;
import lombok.NonNull;

public class StaticNode extends CommandNode<StringNodeKey> {

  public StaticNode(@NonNull String string) {
    super(NodeKey.ofStatic(string));
  }
}
