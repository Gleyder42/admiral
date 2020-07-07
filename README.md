## Overview

This project requires Java 14.  
Methods never return null. If they would, an `Optional` is used. 

Admiral is a platform independent command framework. A command is build in tree-based way.

The project consists of two projects: `core` and `annotation-builder`. The `core` implements the    
functionality, while providing a simple command node builder to create `CommandNode`s.
The `annotation-builder` gives you the possibility to create commands completely via annotations.

### Add the dependency

Gradle via Groovy. You probably need to shadow it. Use the [gradle shadow plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow).
```groovy
dependencies {
    compile 'de.gleyder.admiral:core:1.0'
    compile 'de.gleyder.admiral:annotation-builder:1.0'
}
```

### Use it in your code

An admiral command is essentially a tree. Meaning, you have static nodes, like
`echo` in `/test echo <value>` and dynamic nodes, like `<value>`.

First, create a `CommandDispatcher`. The dispatcher is your anchor. 
You register and dispatch commands through it.
 
```java
CommandDisptacher dispatcher = new CommandDispatcher();
```

Secondly, create the desired nodes. We want to create `/echo <amount> <message>`.
```java
StaticNode echoNode = new StaticNodeBuilder("echo")
    .setExecutor(context -> {
      int amount = context.getBag().get("amount", Integer.class).orElseThrow();
      String message = context.getBag().get("message", String.class).orElseThrow();

      for (int i = 0; i < amount; i++) {
        System.out.println("Nr. " + i + " " + message);
      }
    })
    .build();
DynamicNode amountNode = new DynamicNodeBuilder("amount")
    .setInterpreter(CommonInterpreter.INT)
    .build()

DynamicNode messageNode = new DynamicNodeBuilder("message")
    .build();
```

First, create a static node `echo`. The `echo` node holds the executor.  
Then create a dynamic node `<amount>` and give
it a `CommonInterpreter.INT`, meaning the argument will be parsed to an int.
The `<message>` dynamic node has no interpreter, as the string interpreter is already
default.

Then wire them up. `addNode` returns the parameter node.
`echoNode.addNode(amountNode)` will return `amountNode`.  

```java
echoNode.addNode(amountNode).addNode(messageNode);
```

After that, register the command.

```java
dispatcher.registerCommand(echoNode);
```

Because admiral is platform independent, you need to implement it
yourself. 

When you set everything up and want to execute the command,dispatch the command. 
The first argument is the command, the second one is the source. The source can
be any object. The last argument is the interpreter map (`Map<String, Object>`).
Via the interpreter map you can carry additional info to the interpreters.

```java
dispatcher.dispatch("echo 10 (Hello World)", new Object(), Collections.emptyMap());
```