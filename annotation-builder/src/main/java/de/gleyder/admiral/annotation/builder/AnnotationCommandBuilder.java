package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.annotation.builder.producer.*;
import de.gleyder.admiral.core.CommandContext;
import de.gleyder.admiral.core.CommandDispatcher;
import de.gleyder.admiral.core.executor.Executor;
import de.gleyder.admiral.core.interpreter.*;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import de.gleyder.admiral.core.interpreter.strategy.SingleStrategy;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationCommandBuilder {

  private static final Map<Class<? extends Annotation>, NodeProducer<? extends Annotation>> PRODUCER_MAP = Map.of(
          ExecutorNode.class, new MethodExecutorProducer(),
          RequiredNode.class, new MethodRequiredProducer(),
          InterpreterNode.class, new MethodInterpreterProducer(),
          InterpreterStrategyNode.class, new MethodInterpreterStrategyProducer()
  );

  private final Set<Object> registeredClasses = new HashSet<>();
  private final Map<String, Interpreter<?>> registeredInterpreters = new HashMap<>();
  private final Map<String, InterpreterStrategy> registeredInterpreterStrategies = new HashMap<>();

  public AnnotationCommandBuilder() {
    registeredInterpreters.put("byte", new ByteInterpreter());
    registeredInterpreters.put("short", new ShortInterpreter());
    registeredInterpreters.put("int", new IntegerInterpreter());
    registeredInterpreters.put("long", new LongInterpreter());
    registeredInterpreters.put("float", new FloatInterpreter());
    registeredInterpreters.put("double", new DoubleInterpreter());
    registeredInterpreters.put("char", new CharacterInterpreter());
    registeredInterpreters.put("boolean", new BooleanInterpreter());
    registeredInterpreters.put("string", new StringInterpreter());

    registeredInterpreterStrategies.put("single", new SingleStrategy());
    registeredInterpreterStrategies.put("merged", new MergedStrategy());
  }

  public AnnotationCommandBuilder registerInterpreter(@NonNull String key, @NonNull Interpreter<?> interpreter) {
    registeredInterpreters.put(key, interpreter);
    return this;
  }

  public AnnotationCommandBuilder registerInterpreterStrategy(@NonNull String key, @NonNull InterpreterStrategy strategy) {
    registeredInterpreterStrategies.put(key, strategy);
    return this;
  }

  public AnnotationCommandBuilder registerCommand(@NonNull Object instance) {
    registeredClasses.add(instance);
    return this;
  }

  public void build(@NonNull CommandDispatcher dispatcher) {
    registeredClasses.stream()
            .filter(object -> object.getClass().isAnnotationPresent(Command.class))
            .map(this::toNode)
            .forEach(dispatcher::registerCommand);
  }

  @SneakyThrows
  private StaticNode toNode(Object instance) {
    Class<?> aClass = instance.getClass();
    StaticNode rootNode = new StaticNode(aClass.getAnnotation(Command.class).value());
    Map<String, Object> nodeMap = new HashMap<>();

    Arrays.stream(aClass.getMethods()).forEach(method ->
            PRODUCER_MAP.entrySet().stream()
              .filter(entry -> method.isAnnotationPresent(entry.getKey()))
              .findFirst().ifPresent(producerEntry -> {
        NodeProducer<Annotation> producer = (NodeProducer<Annotation>) producerEntry.getValue();
        nodeMap.put(producer.getKey(method.getAnnotation(producerEntry.getKey()), method), producer.produce(instance, method));
      }));


    Arrays.stream(aClass.getMethods())
            .filter(method -> method.isAnnotationPresent(Route.class) && method.isAnnotationPresent(ExecutorNode.class))
            .forEach(method -> {
              Deque<Node> nodeDeque = Arrays.stream(method.getAnnotation(Route.class).value())
                      .collect(Collectors.toCollection(ArrayDeque::new));
              CommandNode lastNode = rootNode;

              while (!nodeDeque.isEmpty()) {
                Node node = nodeDeque.pop();
                CommandNode currentNode;

                if (!node.interpreter().isEmpty() || !node.strategy().isEmpty()) {
                  currentNode = new DynamicNode(node.value());
                  DynamicNode dynamicNode = (DynamicNode) currentNode;

                  if (!node.interpreter().isEmpty()) {
                    Interpreter<?> interpreter = getInterpreter(node.interpreter(), nodeMap);
                    if (interpreter == null) {
                      throw new NullPointerException("No interpreter was registered with key " + node.interpreter());
                    }

                    dynamicNode.setInterpreter(interpreter);
                  }

                  if (!node.strategy().isEmpty()) {
                    InterpreterStrategy interpreterStrategy = getInterpreterStrategy(node.strategy(), nodeMap);
                    if (interpreterStrategy == null) {
                      throw new NullPointerException("No interpreter strategy was registered with key " + node.strategy());
                    }

                    dynamicNode.setInterpreterStrategy(interpreterStrategy);
                  }
                } else {
                  currentNode = new StaticNode(node.value());
                }

                if (!node.executor().isEmpty()) {
                  Executor executor = (Executor) nodeMap.get(node.executor());
                  if (executor == null) {
                    throw new NullPointerException("No executor found with key " + node.executor());
                  }

                  currentNode.setExecutor(executor);
                }

                if (!node.required().isEmpty()) {
                  Predicate<CommandContext<? super Object>> required = (Predicate<CommandContext<? super Object>>) nodeMap.get(node.required());
                  if (required == null) {
                    throw new NullPointerException("No required node found with key " + node.required());
                  }

                  currentNode.setRequired(required);
                }

                if (nodeDeque.isEmpty()) {
                  NodeProducer<? extends Annotation> producer = PRODUCER_MAP.get(ExecutorNode.class);
                  currentNode.setExecutor((Executor) producer.produce(instance, method));
                }

                lastNode.addNode(currentNode);
                lastNode = currentNode;
              }
            });
    return rootNode;
  }

  private Interpreter<?> getInterpreter(@NonNull String key, @NonNull Map<String, Object> nodeMap) {
    Interpreter<?> interpreter = registeredInterpreters.get(key);
    if (interpreter != null) {
      return interpreter;
    } else {
      return (Interpreter<?>) nodeMap.get(key);
    }
  }

  private InterpreterStrategy getInterpreterStrategy(@NonNull String key, @NonNull Map<String, Object> nodeMap) {
    InterpreterStrategy strategy = registeredInterpreterStrategies.get(key);
    if (strategy != null) {
      return strategy;
    } else {
      return (InterpreterStrategy) nodeMap.get(key);
    }
  }
}
