# TaskBuddy

## What is TaskBuddy?

TaskBuddy is your friend in your laptop that helps you manage your tasks easily. You can enter commands with tasks in the command line tool to add, remove, edit and list tasks. With TaskBuddy, never forget your tasks!

## How to install?

## How to use?

## Developer guide

### IDE Setup

* Download and install version 1.8 of the Java runtime environment and eclipse. 
* Create a new java project, choosing the local copy of this repo as a source folder.
* Download the latest version of [Gson](https://github.com/google/gson) and add the .jar to the build path.

### Architecture Overview

TaskBuddy consists of the following components: 
* UI, which reads user input, formats and displays information
* command parser, which extracts information about commands from user input
* logic, which implements TaskBuddy's functionality by calling the APIs of other components,
* storage manager, which reads and writes from a file containing task data
* task and command, which contains information about tasks and commands respectively

### UI

### Logic

API:
* public static void processUserInput(String userInput) throws Exception: executes the requested command

### CommandParser

API:
*     public static Command getCommandFromInput(String input): returns a command object which contains information about the command to be performed.

### StorageManager

* void openStorage(): Opens the storage by creating storage file in json format where Tasks will be written.
* void closesStorage(): Closes the storage after use.

* Task[] readTask(String name): Get all tasks that corresponds to the given input name.
* Task[] readAllTask(): Get all tasks in the storage file.

* void writeTask(Task task): Write given input task to the storage file.
* void removeTask(Task task): Remove given input task from the storage file.
