package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.annotation.builder.producer.*;
import de.gleyder.admiral.core.CommandDispatcher;
import de.gleyder.admiral.core.executor.Check;
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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationCommandBuilder {

  private static final Map<Class<? extends Annotation>, NodeProducer<? extends Annotation>> PRODUCER_MAP = Map.of(
      ExecutorNode.class, new MethodExecutorProducer(),
      CheckNode.class, new MethodCheckProducer(),
      InterpreterNode.class, new MethodInterpreterProducer(),
      InterpreterStrategyNode.class, new MethodInterpreterStrategyProducer()
  );

  private final Set<Object> registeredClasses = new HashSet<>();
  private final Map<String, Interpreter<?>> registeredInterpreters = new HashMap<>();
  private final Map<String, InterpreterStrategy> registeredInterpreterStrategies = new HashMap<>();
  private final Map<String, Check> registeredChecks = new HashMap<>();
  private final Map<String, Executor> registeredExecutors = new HashMap<>();

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

  public AnnotationCommandBuilder registerExecutor(@NonNull String key, @NonNull Executor executor) {
    registeredExecutors.put(key, executor);
    return this;
  }

  public AnnotationCommandBuilder registerCheck(@NonNull String key, @NonNull Check check) {
    registeredChecks.put(key, check);
    return this;
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
    if (!instance.getClass().isAnnotationPresent(Node.class)) {
      throw new IllegalStateException("Class must be annotated with @Node");
    }

    registeredClasses.add(instance);
    return this;
  }

  public void build(@NonNull CommandDispatcher dispatcher) {
    registeredClasses.stream()
        .map(this::toNode)
        .forEach(dispatcher::registerCommand);
  }

  @SneakyThrows
  private StaticNode toNode(Object instance) {
    Class<?> instanceClass = instance.getClass();
    Node rootAnnotation = instanceClass.getAnnotation(Node.class);
    StaticNode rootNode = new StaticNode(rootAnnotation.value());
    rootNode.getAliases().addAll(Arrays.asList(rootAnnotation.aliases()));
    Map<String, Object> nodeMap = new HashMap<>();

    Arrays.stream(instanceClass.getMethods()).forEach(method -> PRODUCER_MAP.entrySet().stream()
        .filter(entry -> method.isAnnotationPresent(entry.getKey())).findFirst().ifPresent(producerEntry -> {
          NodeProducer<Annotation> producer = (NodeProducer<Annotation>) producerEntry.getValue();
          nodeMap.put(
              producer.getKey(method.getAnnotation(producerEntry.getKey()), method),
              producer.produce(instance, method)
          );
        }));

    applyNodeAnnotation(nodeMap, rootAnnotation, rootNode);

    Arrays.stream(instanceClass.getMethods())
        .filter(method -> method.isAnnotationPresent(Route.class))
        .forEach(method -> {
          Deque<Node> nodeDeque = Arrays.stream(method.getAnnotation(Route.class).value())
              .collect(Collectors.toCollection(ArrayDeque::new));
          CommandNode lastNode = rootNode;

          while (!nodeDeque.isEmpty()) {
            Node node = nodeDeque.pop();
            CommandNode currentNode;

            if (!node.interpreter().isEmpty() || !node.strategy().isEmpty()) {
              currentNode = new DynamicNode(node.value());
            } else {
              currentNode = new StaticNode(node.value());
            }

            applyNodeAnnotation(nodeMap, node, currentNode);

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

  private void trySetAliases(@NonNull Node node, @NonNull CommandNode currentNode) {
    if (node.aliases().length > 0) {
      ensureNodeType(currentNode, NodeType.STATIC);
      var staticNode = (StaticNode) currentNode;
      staticNode.getAliases().addAll(Arrays.asList(node.aliases()));
    }
  }

  private void trySetInterpreterStrategyNode(@NonNull Map<String, Object> nodeMap, @NonNull Node node,
                                             @NonNull CommandNode currentNode) {
    if (!node.strategy().isEmpty()) {
      ensureNodeType(currentNode, NodeType.DYNAMIC);
      var dynamicNode = (DynamicNode) currentNode;

      InterpreterStrategy interpreterStrategy = getInterpreterStrategy(node.strategy(), nodeMap);
      if (interpreterStrategy == null) {
        throw new NullPointerException("No interpreter strategy was registered with key " + node.strategy());
      }

      dynamicNode.setInterpreterStrategy(interpreterStrategy);
    }
  }

  private void trySetInterpreterNode(@NonNull Map<String, Object> nodeMap, @NonNull Node node, @NonNull CommandNode currentNode) {
    if (!node.interpreter().isEmpty()) {
      ensureNodeType(currentNode, NodeType.DYNAMIC);
      var dynamicNode = (DynamicNode) currentNode;

      Interpreter<?> interpreter = getInterpreter(node.interpreter(), nodeMap);
      if (interpreter == null) {
        throw new NullPointerException("No interpreter was registered with key " + node.interpreter());
      }

      dynamicNode.setInterpreter(interpreter);
    }
  }

  private void trySetExecutorNode(@NonNull Map<String, Object> nodeMap, @NonNull Node node, @NonNull CommandNode currentNode) {
    if (!node.executor().isEmpty()) {
      Executor executor = (Executor) nodeMap.get(node.executor());
      if (executor == null) {
        executor = registeredExecutors.get(node.executor());
      }

      if (executor == null) {
        throw new NullPointerException("No executor found with key " + node.executor());
      }

      currentNode.setExecutor(executor);
    }
  }

  private void trySetCheck(@NonNull Map<String, Object> nodeMap, @NonNull Node node, @NonNull CommandNode currentNode) {
    currentNode.setCheck(Check.ofMultiple(Arrays.stream(node.check())
        .map(checkName -> {
          Check check = (Check) nodeMap.get(checkName);
          if (check == null) {
            check = registeredChecks.get(checkName);
          }
          if (check == null) {
            throw new NullPointerException("No required node found with key " + checkName);
          }
          return check;
        }).collect(Collectors.toList())));
  }

  private Interpreter<?> getInterpreter(@NonNull String key, @NonNull Map<String, Object> nodeMap) {
    return getInterpreterInternal(key, nodeMap, () -> registeredInterpreters.get(key));
  }

  private InterpreterStrategy getInterpreterStrategy(@NonNull String key, @NonNull Map<String, Object> nodeMap) {
    return getInterpreterInternal(key, nodeMap, () -> registeredInterpreterStrategies.get(key));
  }

  private <T> T getInterpreterInternal(@NonNull String key, @NonNull Map<String, Object> nodeMap, @NonNull Supplier<T> supplier) {
    T t = supplier.get();
    if (t != null) {
      return t;
    } else {
      return (T) nodeMap.get(key);
    }
  }

  private void ensureNodeType(@NonNull CommandNode node, @NonNull NodeType nodeType) {
    if (nodeType == NodeType.DYNAMIC && !(node instanceof DynamicNode)) {
      throw new IllegalStateException("Node " + node + " is not dynamic");
    } else if (nodeType == NodeType.STATIC && !(node instanceof StaticNode)) {
      throw new IllegalStateException("Node " + node + " is not static");
    }
  }

  private void applyNodeAnnotation(@NonNull Map<String, Object> nodeMap, @NonNull Node node, @NonNull CommandNode currentNode) {
    trySetExecutorNode(nodeMap, node, currentNode);
    trySetCheck(nodeMap, node, currentNode);
    trySetInterpreterNode(nodeMap, node, currentNode);
    trySetInterpreterStrategyNode(nodeMap, node, currentNode);
    trySetAliases(node, currentNode);
  }

  private enum NodeType {
    DYNAMIC, STATIC
  }
}
