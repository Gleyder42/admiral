package de.gleyder.admiral.annotation.builder;

import de.gleyder.admiral.annotation.*;
import de.gleyder.admiral.annotation.builder.producer.*;
import de.gleyder.admiral.annotation.executor.MethodExecutor;
import de.gleyder.admiral.annotation.executor.MethodRequired;
import de.gleyder.admiral.core.CommandDispatcher;
import de.gleyder.admiral.core.interpreter.IntegerInterpreter;
import de.gleyder.admiral.core.interpreter.Interpreter;
import de.gleyder.admiral.core.interpreter.strategy.InterpreterStrategy;
import de.gleyder.admiral.core.interpreter.strategy.MergedStrategy;
import de.gleyder.admiral.core.interpreter.strategy.SingleStrategy;
import de.gleyder.admiral.core.node.CommandNode;
import de.gleyder.admiral.core.node.DynamicNode;
import de.gleyder.admiral.core.node.StaticNode;
import de.gleyder.admiral.core.node.key.NodeKey;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class AnnotationCommandBuilder {

  private static final Map<Class<? extends Annotation>, NodeProducer<? extends Annotation>> PRODUCER_MAP = Map.of(
          ExecutorNode.class, new MethodExecutorProducer(),
          RequiredNode.class, new MethodRequiredProducer(),
          InterpreterNode.class, new MethodInterpreterProducer(),
          InterpreterStrategyNode.class, new MethodInterpreterStrategyProducer()
  );

  private final Set<Object> registeredClasses = new HashSet<>();
  private final Map<Class<?>, Interpreter<?>> registeredInterpreters = new HashMap<>();
  private final Map<String, InterpreterStrategy> registeredInterpreterStrategies = new HashMap<>();

  public AnnotationCommandBuilder() {
    registeredInterpreters.put(Integer.class, new IntegerInterpreter());

    registeredInterpreterStrategies.put(SingleStrategy.class.getSimpleName(), new SingleStrategy());
    registeredInterpreterStrategies.put(MergedStrategy.class.getSimpleName(), new MergedStrategy());
  }

  public AnnotationCommandBuilder registerInterpreter(@NonNull Class<?> aClass, @NonNull Interpreter<?> interpreter) {
    registeredInterpreters.put(aClass, interpreter);
    return this;
  }

  public AnnotationCommandBuilder registerInterpreterStrategy(@NonNull String name, @NonNull InterpreterStrategy strategy) {
    registeredInterpreterStrategies.put(name, strategy);
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
    StaticNode commandNode = new StaticNode(aClass.getAnnotation(Command.class).value());
    Map<String, Object> nodeMap = new HashMap<>();

    Arrays.stream(aClass.getMethods()).forEach(method -> Arrays.stream(method.getDeclaredAnnotations())
            .filter(annotation -> method.isAnnotationPresent(annotation.annotationType()))
            .filter(annotation -> PRODUCER_MAP.containsKey(annotation.annotationType()))
            .forEach(annotation -> {
              //noinspection unchecked
              NodeProducer<Annotation> producer = (NodeProducer<Annotation>) PRODUCER_MAP.get(annotation.annotationType());
              nodeMap.put(producer.getKey(annotation), producer.produce(instance, method));
            })
    );

    for (Method method : aClass.getMethods()) {
      if (method.isAnnotationPresent(Route.class)) {
        CommandNode<? extends NodeKey> lastNode = null;
        ArrayDeque<Node> nodeDeque = new ArrayDeque<>(Arrays.asList(method.getAnnotation(Route.class).value()));

        while (!nodeDeque.isEmpty()) {
          Node node = nodeDeque.pop();
          CommandNode<? extends NodeKey> currentNode;
          if (node.type().isAssignableFrom(NoType.class)) {
            currentNode = new StaticNode(node.name());
          } else {
            currentNode = new DynamicNode(node.type(), node.name());
            DynamicNode dynamicNode = (DynamicNode) currentNode;

            if (registeredInterpreters.containsKey(node.type())) {
              dynamicNode.setInterpreter(registeredInterpreters.get(node.type()));
            }

            if (registeredInterpreterStrategies.containsKey(node.strategy())) {
              dynamicNode.setInterpreterStrategy(registeredInterpreterStrategies.get(node.strategy()));
            }
          }

          if (!node.required().isEmpty()) {
            currentNode.setRequired((MethodRequired) nodeMap.get(node.required()));
          }

          if (currentNode instanceof DynamicNode) {
            DynamicNode dynamicNode = (DynamicNode) currentNode;

            if (!node.interpreter().isEmpty()) {
              dynamicNode.setInterpreter((Interpreter<?>) nodeMap.get(node.interpreter()));
            }
          }


          Objects.requireNonNullElse(lastNode, commandNode).addNode(currentNode);
          if (nodeDeque.isEmpty()) {
            if (method.isAnnotationPresent(ExecutorNode.class)) {
              currentNode.setExecutor(new MethodExecutor(instance, method));
            } else if (method.isAnnotationPresent(RequiredNode.class)) {
              currentNode.setRequired(new MethodRequired(instance, method));
            }
          }
          lastNode = currentNode;
        }
      }
    }
    return commandNode;
  }
}
