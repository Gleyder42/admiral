package de.gleyder.admiral.node;

import de.gleyder.admiral.CommandContext;
import de.gleyder.admiral.interpreter.Interpreter;
import de.gleyder.admiral.interpreter.InterpreterResult;
import de.gleyder.admiral.interpreter.StringInterpreter;
import de.gleyder.admiral.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.interpreter.strategy.MergedStrategy;
import de.gleyder.admiral.node.key.NodeKey;
import de.gleyder.admiral.node.key.TypeNodeKey;
import de.gleyder.admiral.parser.InputArgument;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class DynamicNode extends CommandNode<TypeNodeKey<?>> {

  @Setter
  @Getter
  private InterpreterStrategy interpreterStrategy = new MergedStrategy();

  @Setter
  @Getter
  private Interpreter<?> interpreter = new StringInterpreter();

  public DynamicNode(@NonNull Class<?> aClass, @NonNull String key) {
    super(NodeKey.ofDynamic(aClass, key));
  }

  @Override
  public void onCommandProcess(@NonNull CommandContext<?> context, @NonNull Map<String, Object> interpreterMap, @NonNull InputArgument inputArgument) {
    List<InterpreterResult<Object>> results = interpreterStrategy.test(interpreter, inputArgument, interpreterMap);

    results.forEach(result -> {
      if (result.succeeded()) {
        context.getBag().add(getKey().get(), result.getValue().orElseThrow());
      } else {
        result.getError().ifPresent(Throwable::printStackTrace);
      }
    });
  }
}
