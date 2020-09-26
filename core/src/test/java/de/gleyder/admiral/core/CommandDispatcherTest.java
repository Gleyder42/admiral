package de.gleyder.admiral.core;

import de.gleyder.admiral.core.builder.DynamicNodeBuilder;
import de.gleyder.admiral.core.builder.StaticNodeBuilder;
import de.gleyder.admiral.core.error.AmbiguousCommandError;
import de.gleyder.admiral.core.error.CommandError;
import de.gleyder.admiral.core.error.LiteralCommandError;
import de.gleyder.admiral.core.executor.CheckResult;
import de.gleyder.admiral.core.executor.MultipleChecks;
import de.gleyder.admiral.core.interpreter.CommonInterpreter;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CommandDispatcherTest {

  private static final String MESSAGE_KEY = "message";
  private static final String AMOUNT_KEY = "amount";
  private static final String TYPE_KEY = "type";
  private static final String NAME_KEY = "name";
  public static final int DEFAULT_AMOUNT = 10;

  private final CommandDispatcher dispatcher = new CommandDispatcher();

  /*
   * Info Command
   */
  private final StaticNode infoNode = new StaticNodeBuilder("info")
      .setExecutor(context -> log.info("Prints some info"))
      .build();

  /*
   * Number command
   */
  private final StaticNode numberNode = new StaticNode("number");
  private final DynamicNode dynamicNumberEchoNode = new DynamicNodeBuilder("int")
      .setInterpreter(CommonInterpreter.INT)
      .setExecutor(context -> {
        Integer integer = context.getBag().get("int", Integer.class).orElseThrow();
        log.info("Int: {}", integer);
      })
      .build();

  /*
   * Echo Command
   */
  private final StaticNode echoNode = new StaticNodeBuilder("echo")
      .setCheck(context -> createCheckResult(context, MESSAGE_KEY))
      .setExecutor(context -> {
        String message = context.getBag().get(MESSAGE_KEY, String.class).orElseThrow();
        log.info("Message: {}", message);
      })
      .build();

  private final DynamicNode messageNode = new DynamicNode("message");

  /*
   * Item Command
   */
  private final StaticNode itemNode = new StaticNodeBuilder("item")
      .build();

  private final StaticNode createNode = new StaticNodeBuilder("create")
      .setCheck(context -> createCheckResult(context, TYPE_KEY))
      .setExecutor(context -> {
        String type = context.getBag().get(TYPE_KEY, String.class).orElseThrow();
        int amount = context.getBag().get(AMOUNT_KEY, Integer.class).orElse(1);
        String name = context.getBag().get(NAME_KEY, String.class).orElse("undefined");

        log.info("Item create as {}, {} times with name {}", type, amount, name);
      })
      .build();

  private final StaticNode deleteNode = new StaticNodeBuilder("delete")
      .setCheck(context -> createCheckResult(context, NAME_KEY))
      .setExecutor(context -> {
        String name = context.getBag().get(NAME_KEY, String.class).orElseThrow();

        log.info("Item deleted with name {}", name);
      })
      .build();

  private final DynamicNode nameNode = new DynamicNode(NAME_KEY);

  private final DynamicNode amountNode = new DynamicNodeBuilder(AMOUNT_KEY)
      .setCheck(context -> CheckResult.ofSimpleError(
          () -> context.getBag().get(AMOUNT_KEY, Integer.class).orElseThrow() > 0,
          "Amount needs to be at least 1"))
      .setInterpreter(CommonInterpreter.INT)
      .build();

  private final DynamicNode typeNode = new DynamicNode(TYPE_KEY);

  private CheckResult createCheckResult(CommandContext context, String key) {
    return CheckResult.ofSimpleError(
        () -> context.getBag().contains(key),
        "Bag does not contains " + key
    );
  }

  /*
   * Double Command
   */
  private final StaticNode doubleNode = new StaticNodeBuilder("double")
      .build();

  private final DynamicNode stringNode = new DynamicNodeBuilder("string")
      .setExecutor(context -> log.info("String: " + context.getBag().get("string")))
      .build();

  private final DynamicNode intNode = new DynamicNodeBuilder("int")
      .setInterpreter(CommonInterpreter.INT)
      .setExecutor(context -> log.info("Int: " + context.getBag().get("int")))
      .build();

  /*
   * Group commands
   */
  private final List<String> groupList = new ArrayList<>();

  private final StaticNode groupNode = new StaticNode("group");
  private final StaticNode groupCreateNode = new StaticNodeBuilder("create")
      .setExecutor(context -> {
        Optional<String> name = context.getBag().get("name", String.class);
        log.info("Create group with name {}", name.orElseThrow());
        groupList.add(name.get());
      })
      .build();

  private final StaticNode groupRemoveNode = new StaticNodeBuilder("remove")
      .setExecutor(context -> {
        Optional<String> name = context.getBag().get("name", String.class);
        log.info("Deleted group with name {}", name.orElseThrow());
        groupList.remove(name.get());
      })
      .build();

  private final StaticNode groupAddUserNode = new StaticNodeBuilder("addUser")
      .setExecutor(context -> {
        Optional<String> name = context.getBag().get("name", String.class);
        Optional<String> user = context.getBag().get("user", String.class);

        log.info("Add user {} to group with name {}", user.orElseThrow(), name.orElseThrow());
      })
      .build();

  private final DynamicNode groupUserNode = new DynamicNode("user");
  private final DynamicNode simpleNameNode = new DynamicNodeBuilder("name")
      .build();
  private final DynamicNode checkNameNode = new DynamicNodeBuilder("name")
      .setCheck(context ->
          CheckResult.ofSimpleError(
              () -> groupList.contains(context.getBag().get("name", String.class).orElseThrow()),
              "Group not found"
          ))
      .build();

  /*
   * Checks test
   */
  private final StaticNode checksNode = new StaticNodeBuilder("checks")
      .build();

  private final StaticNode allMultipleChecksNode = new StaticNodeBuilder("multiple-all")
      .setCheck(new MultipleChecks(List.of(
          context -> CheckResult.ofSuccessful(), context -> CheckResult.ofSuccessful()
      ), MultipleChecks.Type.ALL))
      .build();

  private final StaticNode anyMultipleChecksNode = new StaticNodeBuilder("multiple-any")
      .setCheck(new MultipleChecks(List.of(
          context -> CheckResult.ofSuccessful(),
          context -> CheckResult.ofError(LiteralCommandError.create().setMessage("Error"))
      ), MultipleChecks.Type.ANY))
      .build();

  private final StaticNode noneMultipleChecksNode = new StaticNodeBuilder("multiple-none")
      .setCheck(new MultipleChecks(List.of(
          context -> CheckResult.ofError(LiteralCommandError.create().setMessage("Error")),
          context -> CheckResult.ofError(LiteralCommandError.create().setMessage("Error"))
      ), MultipleChecks.Type.NONE))
      .build();

  private final StaticNode firstFailCheckNode = new StaticNodeBuilder("firstCheck")
      .setCheck(context -> CheckResult.ofError(LiteralCommandError.create().setMessage("First Check")))
      .build();

  private final StaticNode secondFailCheckNode = new StaticNodeBuilder("secondCheck")
      .setCheck(context -> CheckResult.ofError(LiteralCommandError.create().setMessage("Second Check")))
      .build();

  private final StaticNode successfulCheckNode = new StaticNodeBuilder("successful")
      .setCheck(context -> CheckResult.ofSuccessful())
      .build();

  @BeforeEach
  void setup() {
    /*
     * Group commands
     */
    groupNode.addNode(groupCreateNode);
    groupNode.addNode(groupRemoveNode);
    groupNode.addNode(groupAddUserNode);

    groupAddUserNode.addNode(groupUserNode).addNode(checkNameNode);
    groupRemoveNode.addNode(checkNameNode);

    groupCreateNode.addNode(simpleNameNode);

    /*
     * Double command
     */
    doubleNode.addNode(stringNode);
    doubleNode.addNode(intNode);

    /*
     * Item Command
     */
    itemNode.addNode(createNode);
    itemNode.addNode(deleteNode);

    typeNode.addNode(nameNode).addNode(amountNode);

    createNode.addNode(typeNode);
    deleteNode.addNode(typeNode);

    /*
     * Check command
     */
    checksNode.addNode(firstFailCheckNode).addNode(secondFailCheckNode);
    checksNode.addNode(successfulCheckNode).addNode(secondFailCheckNode);
    checksNode.addNode(allMultipleChecksNode);
    checksNode.addNode(noneMultipleChecksNode);
    checksNode.addNode(anyMultipleChecksNode);

    /*
     *
     */
    numberNode.addNode(dynamicNumberEchoNode);

    /*
     * Echo Command
     */
    echoNode.addNode(messageNode);

    /*
     * Register command
     */
    dispatcher.registerCommand(numberNode);
    dispatcher.registerCommand(groupNode);
    dispatcher.registerCommand(doubleNode);
    dispatcher.registerCommand(itemNode);
    dispatcher.registerCommand(echoNode);
    dispatcher.registerCommand(infoNode);
    dispatcher.registerCommand(checksNode);
  }

  @Test
  void shouldFailAtFirstNode() {
    String command = "checks firstCheck secondCheck";

    List<CommandError> errors = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    assertIterableEquals(List.of(LiteralCommandError.create().setMessage("First Check")), errors);
  }

  @Test
  void shouldFailAtSecondNode() {
    String command = "checks successful secondCheck";

    List<CommandError> errors = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    assertIterableEquals(List.of(LiteralCommandError.create().setMessage("Second Check")), errors);
  }

  @Test
  void shouldSuccessWithAll() {
    String command = "checks multiple-all";

    List<CommandError> errors = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    assertIterableEquals(List.of(), errors);
  }

  @Test
  void shouldSuccessWithAny() {
    String command = "checks multiple-any";

    List<CommandError> errors = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    assertIterableEquals(List.of(), errors);
  }

  @Test
  void shouldSuccessWithNone() {
    String command = "checks multiple-none";

    List<CommandError> errors = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    assertIterableEquals(List.of(), errors);
  }

  @Test
  void shouldFindNumberCommand() {
    String command = "number " + DEFAULT_AMOUNT;
    Map<String, Object> map = Map.of("int", DEFAULT_AMOUNT);

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, map, numberNode, dynamicNumberEchoNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldNotFindNumberCommandWithStringInput() {
    String command = "number Test";

    List<CommandError> dispatch = dispatcher.dispatch(command, new Object(), Collections.emptyMap());

    assertEquals("java.lang.NumberFormatException: For input string: \"Test\"", dispatch.get(0).getSimple());
    assertEquals("java.lang.NumberFormatException: For input string: \"Test\"", dispatch.get(0).getDetailed());
  }

  @Test
  void shouldFindInfoCommand() {
    String command = "info";
    Map<String, Object> map = Collections.emptyMap();

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, map, infoNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindEchoCommand() {
    String command = "echo (Hello World)";
    Map<String, Object> map = Map.of("message", "Hello World");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, map, echoNode, messageNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindItemCreateCommand() {
    String command = "item create String Name 10";
    Map<String, Object> map = Map.of(
        "name", "Name",
        "amount", DEFAULT_AMOUNT,
        "type", "String"
    );

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, map, itemNode, createNode, typeNode, nameNode, amountNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindItemDeleteCommand() {
    String command = "item delete String Name 10";
    Map<String, Object> map = Map.of(
        "name", "Name",
        "amount", DEFAULT_AMOUNT,
        "type", "String"
    );

    CommandRoute actual = findRoute(command);
    System.out.println(actual);

    assertEqualsRoute(actual, map, itemNode, deleteNode, typeNode, nameNode, amountNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindItemCreateCommandWithOnlyTypeAndName() {
    String command = "item create String Name";
    Map<String, Object> bag = Map.of(
        "name", "Name",
        "type", "String"
    );

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, itemNode, createNode, typeNode, nameNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindItemCreateCommandWithOnlyType() {
    String command = "item create String";
    Map<String, Object> bag = Map.of("type", "String");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, itemNode, createNode, typeNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldReturnError() {
    List<CommandError> errorList = dispatcher.dispatch("item", new Object(), Collections.emptyMap());

    assertIterableEquals(
        List.of(LiteralCommandError
            .create()
            .setSimple("No matching command could be found for the specified parameters")
            .setDetailed("The route has no executor")),
        errorList
    );
  }

  @Test
  void shouldFindCommandWithString() {
    String command = "double (Hello World)";
    Map<String, Object> bag = Map.of("string", "Hello World");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, doubleNode, stringNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldNotFindCommandWithInt() {
    String command = "double 10";

    CommandRoute actual = findRoute(command);
    CommandError error = actual.getErrors().get(0);

    assertTrue(error instanceof AmbiguousCommandError);
    AmbiguousCommandError ambiguousCommandError = (AmbiguousCommandError) error;

    assertEqualsRoute(ambiguousCommandError.getRouteList().get(0), Map.of("string", "10"), doubleNode, stringNode);
    assertEqualsRoute(ambiguousCommandError.getRouteList().get(1), Map.of("int", DEFAULT_AMOUNT), doubleNode, intNode);
  }

  @Test
  void shouldFindGroupCreateCommand() {
    String command = "group create (Group Name)";
    Map<String, Object> bag = Map.of("name", "Group Name");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, groupNode, groupCreateNode, simpleNameNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindGroupDeleteCommand() {
    String command = "group remove (Group Name)";
    Map<String, Object> bag = Map.of("name", "Group Name");
    groupList.add("Group Name");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, groupNode, groupRemoveNode, checkNameNode);
    assertNoCommandError(command);
  }

  @Test
  void shouldFindAddUserCommand() {
    String command = "group addUser Gleyder (Group Name)";
    Map<String, Object> bag = Map.of(
        "user", "Gleyder",
        "name", "Group Name"
    );
    groupList.add("Group Name");

    CommandRoute actual = findRoute(command);

    assertEqualsRoute(actual, bag, groupNode, groupAddUserNode, groupUserNode, checkNameNode);
    assertNoCommandError(command);
  }

  @Nested
  class CommandValidation {

    @TestFactory
    List<DynamicTest> allCommandsShouldBeValid() {
      return dispatcher.getAllRoutes().stream()
          .map(route -> DynamicTest.dynamicTest(route.getNodeList().toString(), () -> assertTrue(route.isValid())))
          .collect(Collectors.toUnmodifiableList());
    }

  }

  private CommandRoute findRoute(String command) {
    return dispatcher.findRoute(command, Collections.emptyMap());
  }

  private void assertNoCommandError(String command) {
    List<CommandError> errorList = dispatcher.dispatch(command, new Object(), Collections.emptyMap());
    System.out.println(errorList);
    assertIterableEquals(emptyList(), errorList);
  }

  private void assertEqualsRoute(CommandRoute actual, Map<String, Object> bag, CommandNode... nodes) {
    List<CommandNode> nodeList = new ArrayList<>(nodes.length + 1);
    if (nodes.length > 0) {
      nodeList.add(dispatcher.getRootNode());
    }
    nodeList.addAll(Arrays.asList(nodes));
    ValueBag valueBag = new ValueBag();
    bag.forEach(valueBag::add);
    assertIterableEquals(nodeList, actual.getNodeList());
    assertEquals(valueBag, actual.getValueBag());
  }

}