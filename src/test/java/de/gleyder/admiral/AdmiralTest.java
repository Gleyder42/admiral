package de.gleyder.admiral;

import de.gleyder.admiral.interpreter.IntegerInterpreter;
import de.gleyder.admiral.node.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AdmiralTest {

  private final CommandSolver solver = new CommandSolver();

  private final StaticNode testNode = new StaticNode("test");
  private final StaticNode echoNode = new StaticNodeBuilder("echo")
          .setExecutor(context -> log.info("This echo node has an executor but not a value!"))
          .build();
  private final StaticNode createNode = new StaticNode("create");

  private final DynamicNode stringLogNode = new DynamicNodeBuilder(String.class, "string")
          .setExecutor(context -> log.info("Echo {}", context.getBag().get("string").orElseThrow()))
          .build();

  private final DynamicNode integerLogNode = new DynamicNodeBuilder(Integer.class, "int")
          .setExecutor(context -> log.info("Int Echo: {}", context.getBag().get("int").orElseThrow()))
          .setInterpreter(new IntegerInterpreter())
          .build();

  private final StaticNode optionalNode = new StaticNodeBuilder("optional")
          .setExecutor(context -> {
            Optional<Integer> anInt = context.getBag().get("int");
            Optional<String> string = context.getBag().get("string");

            log.info("Echo Int: {} String: {}", anInt.orElse(-1), string.orElse("haus"));
          })
          .build();

  @BeforeEach
  void setup() {
    echoNode.addNode(integerLogNode);
    echoNode.addNode(stringLogNode);

    testNode.addNode(echoNode);
    testNode.addNode(createNode);

    createNode.addNode(optionalNode);
    createNode.addNode(integerLogNode);
    integerLogNode.addNode(stringLogNode);
    stringLogNode.addNode(optionalNode);

    solver.registerCommand(testNode);
  }

  @Test
  void shouldFindOptionalNodeWithValue() {
    CommandRoute actual = solver.findRoute("test create 10 test optional");

    assertEqualsRoute(actual, testNode, createNode, integerLogNode, stringLogNode, optionalNode);
  }

  @Test
  void shouldFindOptionalNodeWithoutValues() {
    CommandRoute actual = solver.findRoute("test create optional");

    assertEqualsRoute(actual, testNode, createNode, optionalNode);
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