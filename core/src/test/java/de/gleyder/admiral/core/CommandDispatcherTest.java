package de.gleyder.admiral.core;

import de.gleyder.admiral.core.builder.DynamicNodeBuilder;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.interpreter.CommonInterpreter;
import de.gleyder.admiral.core.interpreter.IntegerInterpreter;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CommandDispatcherTest {

  private final CommandDispatcher dispatcher = new CommandDispatcher();

  private final StaticNode testNode = new StaticNodeBuilder("test")
          .addAlias("t")
          .build();
  private final StaticNode echoNode = new StaticNodeBuilder("echo")
          .setExecutor(context -> log.info("This echo node has an executor but not a value!"))
          .build();
  private final StaticNode createNode = new StaticNodeBuilder("create")
          .addAlias("c")
          .build();
  private final StaticNode requireNode = new StaticNodeBuilder("required")
          .setRequired(context -> context.getBag().get("int").isPresent())
          .build();
  private final DynamicNode stringLogNode = new DynamicNodeBuilder("string")
          .setInterpreterStrategy(new MergedStrategy())
          .setExecutor(context -> log.info("Echo {}", context.getBag().get("string").orElseThrow()))
          .build();
  private final DynamicNode integerLogNode = new DynamicNodeBuilder("int")
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
  private final StaticNode sumNode = new StaticNodeBuilder("sum")
          .setExecutor(context -> {
            int n1 = context.getBag().get("n1", Integer.class).orElseThrow();
            int n2 = context.getBag().get("n2", Integer.class).orElseThrow();

            log.info("Result: " + (n1 + n2));
          })
          .build();
  private final DynamicNode n1Node = new DynamicNodeBuilder("n1")
          .setInterpreter(CommonInterpreter.INT)
          .build();
  private final DynamicNode n2Node = new DynamicNodeBuilder("n2")
          .setInterpreter(CommonInterpreter.INT)
          .build();

  @BeforeEach
  void setup() {
    integerLogNode.addNode(stringLogNode);
    integerLogNode.addNode(requireNode);

    stringLogNode.addNode(optionalNode);

    sumNode.addNode(n1Node).addNode(n2Node);

    createNode.addNode(optionalNode);
    createNode.addNode(integerLogNode);

    echoNode.addNode(integerLogNode);
    echoNode.addNode(stringLogNode);
    echoNode.addNode(requireNode);

    testNode.addNode(echoNode);
    testNode.addNode(createNode);
    testNode.addNode(sumNode);

    dispatcher.registerCommand(testNode);
  }

  @Test
  void shouldFindOptionalNodeWihValueAndAlias() {
    CommandRoute actual = dispatcher.findRoute("t c 10 test optional");

    assertEqualsRoute(actual, testNode, createNode, integerLogNode, stringLogNode, optionalNode);
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
    CommandRoute expected = new CommandRoute();
    expected.add(dispatcher.getRootNode());
    expected.add(testNode);

    expected.addError(new CommandDispatcherException("No executor was found for node test"));

    assertEquals(expected.getErrors().get(0).getMessage(), actual.getErrors().get(0).getMessage());
    assertEquals(expected.getNodeList(), actual.getNodeList());
  }

  @Test
  void shouldFindEchoNode() {
    CommandRoute actual = dispatcher.findRoute("test echo");

    assertEqualsRoute(actual, testNode, echoNode);
  }

  @Test
  void shouldThrowCommandSolverException() {
    assertThrows(AmbiguousCommandRouteException.class, () -> dispatcher.findRoute("test echo 10"));
  }

  @Test
  void shouldRouteWithRequired() {
    CommandRoute actual = dispatcher.findRoute("test echo 10 required");

    assertEqualsRoute(actual, testNode, echoNode, integerLogNode, requireNode);
  }

  @Test
  void shouldRouteWithoutRequired() {
    CommandRoute actual = dispatcher.findRoute("test echo required");

    assertEqualsRoute(actual, testNode, echoNode, requireNode);
  }

  @Test
  void shouldCalculate() {
    CommandRoute actual = dispatcher.findRoute("test sum 10 10");

    assertEqualsRoute(actual, testNode, sumNode, n1Node, n2Node);
  }

  @Test
  void shouldNotThrowException() {
    assertDoesNotThrow(() -> dispatcher.dispatch("test sum 10 10", new Object(), Collections.emptyMap()));
  }

  @Test
  void shouldThrowExceptionIfRequiredNegative() {
    assertThrows(CommandDispatcherException.class, () -> dispatcher.dispatch("test echo required", new Object(), Collections.emptyMap()));
  }

  @Test
  void shouldNotThrowExceptionIfRequiredPositive() {
    assertDoesNotThrow(() -> dispatcher.dispatch("test echo 10 required", new Object(), new HashMap<>()));
  }

  @TestFactory
  List<DynamicTest> testAllRoutes() {
    return dispatcher.getAllRoutes().stream()
            .map(route -> DynamicTest.dynamicTest(route.getNodeList().toString(),
                    () -> assertTrue(route.isValid()))
            ).collect(Collectors.toUnmodifiableList());
  }

  private void assertEqualsRoute(CommandRoute actual, CommandNode... nodes) {
    List<CommandNode> nodeList = new ArrayList<>(nodes.length + 1);
    if (nodes.length > 0) {
      nodeList.add(dispatcher.getRootNode());
    }
    nodeList.addAll(Arrays.asList(nodes));
    assertEquals(new CommandRoute(nodeList), actual);
  }
}