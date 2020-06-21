package de.gleyder.admiral.core.node;

import de.gleyder.admiral.core.node.key.NodeKey;
import de.gleyder.admiral.core.node.key.StringNodeKey;
import lombok.NonNull;

public class StaticNode extends CommandNode<StringNodeKey> {

  public StaticNode(@NonNull String string) {
    super(NodeKey.ofStatic(string));
  }
}
