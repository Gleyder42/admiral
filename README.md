## Overview

## V2 Changelog

### The Gradle root project no longer configures the subprojects,


instead the subprojects configure themselves.
Essentially, configuration has been moved from the root `build.gradle` to the project's `build.gradle`.
This has the advantage that all information about the subproject is located in it's `build.gradle` making it clear
how the subproject is configured.
With the old approach, it may become hard to track where a configuration is coming from.
In this case, it is still easy to that, but in larger (more nested Gradle projects), it becomes hard to keep track.

Sharing Gradle project configuration is done through [buildSrc](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html) directory.
However, this is not necessary here, because the project configuration is simple.
In this case, code duplication is less bad than the added complexity through the `buildSrc` directory.

This project requires Java 21, instead of 14.

Methods that might return null, ar tagged with `@Nullable`.
Before, they would return an Optional.

On the typesafe side, this is great since it forces the developer to check if the value is present.
However, this has a performance and memory impact.
Everytime a new nullable is created, a new object has to be allocated.
To combat NPEs, the `@Nullable` annotation is used. 
It is checked by IntelliJ, so it will give a hint that a maybe-null object is accessed, if not checked before.
Perhaps, using Optional widely becomes useful once Project Valhalla is done.

### Use Kotlin DSL for the Build scripts instead of Groovy DSL.

Nowadays, I am using the Kotlin DSL for build scripts.
The Tab Completion works way better than with Groovy and Kotlin is more explicit.

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
      int amount = context.getBag().<Integer>get("amount").orElseThrow();
      String message = context.getBag().<String>get("message").orElseThrow();

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

When you set everything up and want to execute the command, dispatch the command. 
The first argument is the command. Normally, arguments are divide by spaces.
`(Hello World)` would be two arguments, if you follow this rule. However, surrounding
`Hello World` by `(` and `)`. The dispatcher will parse it as one argument. 
The second argument is the source. The source can
be any object. The last argument is the interpreter map (`Map<String, Object>`).
Via the interpreter map you can carry additional info to the interpreters.

```java
List<CommandError> erros = dispatcher.dispatch("echo 10 (Hello World)", new Object(), Collections.emptyMap());
```

`dispatcher.dispatch()` returns a `List<CommandError>`. The list is empty, if the command succeeded.
Otherwise, it contains all errors.

`CommandError` has two methods, `getSimple()` and `getDetailed()`. `getDetailed()` is for the
programmers, because it should contain detailed information why the command failed. This can be technical. 

`getSimple()` is for those, who just want to use the command.