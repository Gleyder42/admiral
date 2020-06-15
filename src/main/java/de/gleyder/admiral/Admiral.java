package de.gleyder.admiral;

import de.gleyder.admiral.interpreter.IntegerInterpreter;
import de.gleyder.admiral.interpreter.strategy.SingleStrategy;
import de.gleyder.admiral.node.CommandNode;
import de.gleyder.admiral.node.StringNodeKey;
import de.gleyder.admiral.node.TypeNoteKey;
import de.gleyder.admiral.parser.InputArgument;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Admiral {

  public static void main(String[] args) {
    CommandNode integer = new CommandNode(new TypeNoteKey<>(Integer.class, "integer"));
    integer.setInterpreter(new IntegerInterpreter());
    integer.setInterpreterStrategy(new SingleStrategy());
    integer.setExecutor(context -> log.info("Integer Node: {}", context.getBag().getList("integer").orElseThrow()));

    CommandNode create = new CommandNode(new StringNodeKey("create"));
    create.setExecutor(context -> log.info("Create Node"));

    CommandNode item = new CommandNode(new StringNodeKey("item"));
    item.setExecutor(context -> log.info("Item Node"));

    item.addNode(create);
    create.addNode(integer);

    CommandSolver solver = new CommandSolver();
    solver.registerCommand(item);
    solver.resolve("item create (10 40)", new CommandContext<>(new Object(), new ValueBag()));

    System.out.println(singleLineArguments("Test fuck", "lel e"));
  }

  private static List<InputArgument> singleLineArguments(String... stringArray) {
    return Arrays.stream(stringArray)
        .map(string -> {
          InputArgument inputArgument = new InputArgument();
          inputArgument.getInputs().add(string);
          return inputArgument;
        })
        .collect(Collectors.toUnmodifiableList());
  }
}
