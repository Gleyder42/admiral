package de.gleyder.admiral;

import de.gleyder.admiral.interpreter.IntegerInterpreter;
import de.gleyder.admiral.node.CommandNode;
import de.gleyder.admiral.node.NodeKey;
import de.gleyder.admiral.node.TypeNoteKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AdmiralTest {

  private final CommandSolver solver = new CommandSolver();

  private final CommandNode testNode = new CommandNode(NodeKey.ofStatic("test"));
  private final CommandNode echoNode = new CommandNode(NodeKey.ofStatic("echo"));

  private final CommandNode stringLogNode = new CommandNode(NodeKey.ofDynamic(String.class, "string"));

  private final CommandNode integerLogNode = new CommandNode(NodeKey.ofDynamic(Integer.class, "int"))
          .setInterpreter(new IntegerInterpreter());

  @BeforeEach
  void setup() {
    echoNode.setExecutor(context -> log.info("This echo node has an executor but not a value!"));

    stringLogNode.setExecutor(context -> log.info("Echo {}", context.getBag().get("string").orElseThrow()));
    integerLogNode.setExecutor(context -> log.info("Int Echo: {}", context.getBag().get("int").orElseThrow()));

    echoNode.addNode(integerLogNode);
    echoNode.addNode(stringLogNode);

    testNode.addNode(echoNode);

    solver.registerCommand(testNode);
  }

  @Test
  void shouldFindEchoRoute() {
    CommandRoute actual = solver.findRoute("test echo (Hallo Welt)");
    Exception exception = assertThrows(NumberFormatException.class, () -> {
      Integer.parseInt("Hallo Welt");
    });

    assertIterableEquals(List.of(exception).stream()
            .map((Function<Exception, Object>) Throwable::getMessage).collect(Collectors.toList()),
            actual.getErrors().stream().map(Throwable::getMessage).collect(Collectors.toList())
    );

    assertIterableEquals(
            List.of(solver.getRootNode(), testNode, echoNode, stringLogNode),
            actual.getNodeList()
    );
  }

  @Test
  void shouldNotFindTestRoute() {
    CommandRoute actual = solver.findRoute("test");

    assertEqualsRoute(actual);
  }

  @Test
  void shouldFindEchoNode() {
    CommandRoute actual = solver.findRoute("test echo");

    assertEqualsRoute(actual, testNode, echoNode);
  }

  @Test
  void shouldThrowCommandSolverException() {
    assertThrows(CommandSolverException.class, () -> solver.findRoute("test echo 10"));
  }

  private void assertEqualsRoute(CommandRoute actual, CommandNode... nodes) {
    List<CommandNode> nodeList = new ArrayList<>(nodes.length + 1);
    if (nodes.length > 0) {
      nodeList.add(solver.getRootNode());
    }
    nodeList.addAll(Arrays.asList(nodes));
    assertEquals(new CommandRoute(nodeList), actual);
  }
}