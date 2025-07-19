# Audio Snake
This is a Java project that will play snake game, but there's a twist:

You're controlling the snake using a voice commands!

Say commands: right, left, up, and down to control the direction in which the snake moves.

---
This project was created in Java and uses the Maven automation tool, which makes it easy to use packages/dependencies from all over the web, created by people around the world.

## Here is the list of dependencies we're using:
- Sphinx (audio commands detection)
- JavaFX

## How to run the project:
### Mac/Linux
```
cd audio-snake
mvn exec:java -Dexec.mainClass="com.example.App"
```
### Windows (same as mac apparently)
```
cd audio-snake
mvn exec:java -Dexec.mainClass="com.example.App"
```

## Structure of the codebase
The `audio-snake` directory contains the actual maven project, which includes all the dependencies and code files.

All of the java files for the project are located in this directory: `audio-snake/src/main/java/com/example`
