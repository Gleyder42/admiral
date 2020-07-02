## Introduction

This project requires Java 14. 

Admiral is a platform independent command framework. A command is build in tree-based way.
The project consists of two projects: core and annotation-builder. The core implements the    
functionality, while providing a command node builder to create `CommandNode`s.
The annotation-builder gives you the possibility to create commands completely via annotations.

## How to Use

### Add the dependency

Gradle via Groovy. You probably need to shadow it. Use the [gradle shadow plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow).
```groovy
dependencies {
    compile 'de.gleyder.admiral:core:1.0'
    compile 'de.gleyder.admiral:annotation-builder:1.0'
}
```

### Use it in your code

The `CommandDispatcher` is your anchor. You can register, find and execute commands via the `CommandDispatcher`.
The `CommandDispatcher` executes commands by invoking `CommandDispatcher.dispatch(command, source, map)`.
`CommandDispatcher.dispatch()` returns a list of exceptions, if the given command failed to execute.