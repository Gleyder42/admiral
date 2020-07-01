package de.gleyder.admiral.core.builder;

import de.gleyder.admiral.core.node.StaticNode;
import lombok.NonNull;

import java.util.Arrays;

public class StaticNodeBuilder extends CommandNodeBuilder<StaticNodeBuilder, StaticNode> {

  public StaticNodeBuilder(@NonNull String key) {
    super(new StaticNode(key));
  }

  public StaticNodeBuilder addAlias(@NonNull String alias) {
    node.getAliases().add(alias);
    return this;
  }

  public StaticNodeBuilder addAliasIterable(@NonNull Iterable<String> iterable) {
    iterable.forEach(string -> node.getAliases().add(string));
    return this;
  }

  public StaticNodeBuilder addAliases(@NonNull String... aliases) {
    node.getAliases().addAll(Arrays.asList(aliases));
    return this;
  }
}
