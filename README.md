# Overview

Admiral is a command library designed to be used within in Minecraft plugins.
The project consists of two projects: `core` and `annotation-builder`. The `core` implements the    
functionality, while providing a simple command node builder to create `CommandNode`s.
The `annotation-builder` gives you the possibility to create commands completely via annotations.
Admiral does not use reflections in the `annotation-builder`.
Instead, it uses [ByteBuddy](https://bytebuddy.net/#/) to generate JVM Bytecode at runtime to avoid reflections.
The relevant code in [here](annotation-builder/src/main/java/com/github/gleyder42/annotation/executor/ExecutableMethod.java).

Admiral is now considered outdated, since [PaperMc](https://papermc.io/) now ships with [Mojang's brigadier](https://docs.papermc.io/paper/dev/command-api/basics/introduction/) library.

## Origin

I created `admiral` in February 2021 ([Version 1](https://github.com/Gleyder42/admiral/tree/v1)) as a command library that I can use in my  Minecraft plugins using [PaperMc](https://papermc.io/).
Back in 2021, PaperMc didn't ship with [Mojang's brigadier](https://github.com/Mojang/brigadier) library to create commands, and
`brigadier` wasn't that wildly adopted.
Therefore, I decide to create my own command libray by adopting the "command tree" from brigadier.
The very basic idea is that each command is a tree. 
It has literals (in admiral called StaticNode) that contain the command.
For example "echo". 
Then there are argument nodes (in admiral DyanmicNode) that server as argument.
For example "Hello World".
Together the command would be "/echo Hello World".

## Purpose

Admiral was designed to be a comprehensive command library that allows creating complex commands
while maintaining a readable codebase.

## V2 Changelog

After not having programmed in Java extensively (except for University), I decided to try out some new Java features
and update the library according to my current knowledge (partially).

This changelog highlights the changes I have made.

- Use Java 21 instead of Java 14
- The Gradle root project no longer configures the subprojects
- Do not use Optional everywhere anymore
- Use Kotlin DSL for the Build scripts instead of Groovy DSL.

The paragraphs below explain my decision in more detail.

--- 

### Use Java 21 instead of Java 14

I updated to Java 21, since it is the currently latest LTS version.

### The Gradle root project no longer configures the subprojects

instead the subprojects configure themselves.
Essentially, configuration has been moved from the root `build.gradle` to the project's `build.gradle`.
This has the advantage that all information about the subproject is located in it's `build.gradle` making it clear
how the subproject is configured.
With the old approach, it may become hard to track where a configuration is coming from.
In this case, it is still easy to that, but in larger (more nested Gradle projects), it becomes hard to keep track.

Sharing Gradle project configuration is done through [buildSrc](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html) directory.
However, this is not necessary here, because the project configuration is simple.
In this case, code duplication is less bad than the added complexity through the `buildSrc` directory.

### Do not use Optional everywhere anymore

Methods that might return null, ar tagged with `@Nullable`.
Before, they would return an Optional.

On the typesafe side, this is great since it forces the developer to check if the value is present.
However, this has a performance and memory impact.
Everytime a new nullable is created, a new object has to be allocated.
To combat NPEs, the `@Nullable` annotation is used. 
It is checked by IntelliJ, so it will give a hint that a maybe-null object is accessed, if not checked before.
Perhaps, using Optional widely becomes useful once Project Valhalla is done.

### Use Kotlin DSL for the Build scripts instead of Groovy DSL

Nowadays, I am using the Kotlin DSL for build scripts.
The Tab Completion works way better than with Groovy and Kotlin is more explicit.

## Usage

An admiral command is essentially a tree. Meaning, you have static nodes, like
`echo` in `/test echo <value>` and dynamic nodes, like `<value>`.

First, create a `CommandDispatcher`. The dispatcher is your anchor. 
You register and dispatch commands through it.
 
```java
CommandDisptacher dispatcher = new CommandDispatcher();
```

Secondly, create the desired nodes. We want to create `/echo <amount> <message>`.
```java
void readmeExample(CommandDispatcher dispatcher) {
  StaticNode echoNode = new StaticNodeBuilder("echo")
      .setExecutor(context -> {
        int amount = Objects.requireNonNullElse(context.bag().get("amount"), 0);
        String message = context.bag().get("message");

        for (int i = 0; i < amount; i++) {
          System.out.println(STR."Nr. \{i} \{message}");
        }
      })
      .build();
  DynamicNode amountNode = new DynamicNodeBuilder("amount").setInterpreter(CommonInterpreter.INT).build();
  DynamicNode messageNode = new DynamicNodeBuilder("message").build();

  echoNode.addNode(amountNode).addNode(messageNode);

  dispatcher.registerCommand(echoNode);
}
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
List<CommandError> errors = dispatcher.dispatch("echo 10 (Hello World)", new Object(), Collections.emptyMap());
```

`dispatcher.dispatch()` returns a `List<CommandError>`. The list is empty, if the command succeeded.
Otherwise, it contains all errors.

`CommandError` has two methods, `getSimple()` and `getDetailed()`. `getDetailed()` is for the
programmers, because it should contain detailed information why the command failed. This can be technical. 

`getSimple()` is for those, who just want to use the command.