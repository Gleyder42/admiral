package de.gleyder.admiral.core;

import de.gleyder.admiral.core.builder.DynamicNodeBuilder;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import de.gleyder.admiral.core.node.key.NodeKey;
import de.gleyder.admiral.core.interpreter.IntegerInterpreter;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CommandDispatcherTest {

  private final CommandDispatcher dispatcher = new CommandDispatcher();

  private final StaticNode testNode = new StaticNode("test");
  private final StaticNode echoNode = new StaticNodeBuilder("echo")
          .setExecutor(context -> log.info("This echo node has an executor but not a value!"))
          .build();
  private final StaticNode createNode = new StaticNode("create");
  private final StaticNode requireNode = new StaticNodeBuilder("required")
          .setRequired(context -> context.getBag().get("int").isPresent())
          .build();

  private final DynamicNode stringLogNode = new DynamicNodeBuilder(String.class, "string")
          .setInterpreterStrategy(new MergedStrategy())
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
    echoNode.addNode(requireNode);

    testNode.addNode(echoNode);
    testNode.addNode(createNode);

    createNode.addNode(optionalNode);
    createNode.addNode(integerLogNode);

    integerLogNode.addNode(stringLogNode);
    integerLogNode.addNode(requireNode);

    stringLogNode.addNode(optionalNode);

    dispatcher.registerCommand(testNode);
  }

  @Test
  void shouldFindOptionalNodeWithValue() {
    CommandRoute actual = dispatcher.findRoute("test create 10 test optional");

    assertEqualsRoute(actual, testNode, createNode, integerLogNode, stringLogNode, optionalNode);
  }

  @Test
  void shouldFindOptionalNodeWithoutValues() {
    CommandRoute actual = dispatcher.findRoute("test create optional");

    assertEqualsRoute(actual, testNode, createNode, optionalNode);
  }

  @Test
  void shouldFindEchoRoute() {
    CommandRoute actual = dispatcher.findRoute("test echo (Hallo Welt)");
    Exception exception = assertThrows(NumberFormatException.class, () -> {
      //noinspection ResultOfMethodCallIgnored
      Integer.parseInt("Hallo Welt");
    });

    assertIterableEquals(List.of(exception).stream()
            .map((Function<Exception, Object>) Throwable::getMessage).collect(Collectors.toList()),
            actual.getErrors().stream().map(Throwable::getMessage).collect(Collectors.toList())
    );

    assertIterableEquals(
            List.of(dispatcher.getRootNode(), testNode, echoNode, stringLogNode),
            actual.getNodeList()
    );
  }

  @Test
  void shouldNotFindTestRoute() {
    CommandRoute actual = dispatcher.findRoute("test");

    assertEqualsRoute(actual);
  }

  @Test
  void shouldFindEchoNode() {
    CommandRoute actual = dispatcher.findRoute("test echo");

    assertEqualsRoute(actual, testNode, echoNode);
  }

  @Test
  void shouldThrowCommandSolverException() {
    assertThrows(CommandDispatcherException.class, () -> dispatcher.findRoute("test echo 10"));
  }

  @Test
  void shoutRouteWithRequired() {
    CommandRoute actual = dispatcher.findRoute("test echo 10 required");

    assertEqualsRoute(actual, testNode, echoNode, integerLogNode, requireNode);
  }

  @Test
  void shoutRouteWithoutRequired() {
    CommandRoute actual = dispatcher.findRoute("test echo required");

    assertEqualsRoute(actual, testNode, echoNode, requireNode);
  }

  @Test
  void shouldThrowExceptionIfRequiredNegative() {
    assertThrows(CommandDispatcherException.class, () -> dispatcher.dispatch("test echo required", new Object(), new HashMap<>()));
  }

  @Test
  void shouldNotThrowExceptionIfRequiredPositive() {
    assertDoesNotThrow(() -> dispatcher.dispatch("test echo 10 required", new Object(), new HashMap<>()));
  }

  @SafeVarargs
  private void assertEqualsRoute(CommandRoute actual, CommandNode<? extends NodeKey>... nodes) {
    List<CommandNode<? extends NodeKey>> nodeList = new ArrayList<>(nodes.length + 1);
    if (nodes.length > 0) {
      nodeList.add(dispatcher.getRootNode());
    }
    nodeList.addAll(Arrays.asList(nodes));
    assertEquals(new CommandRoute(nodeList), actual);
  }
}