# TaskBuddy

## What is TaskBuddy?

TaskBuddy is your friend in your laptop that helps you manage your tasks easily. You can enter commands with tasks in the command line tool to add, remove, edit and list tasks. With TaskBuddy, never forget your tasks!

## How to install?

## How to use?

## Public API

### UI

### Logic

### CommandParser

### StorageManager

void openStorage(): Opens the storage by creating storage file in json format where Tasks will be written.
void closesStorage(): Closes the storage after use.

Task[] readTask(String name): Get all tasks that corresponds to the given input name.
Task[] readAllTask(): Get all tasks in the storage file.

void writeTask(Task task): Write given input task to the storage file.
void removeTask(Task task): Remove given input task from the storage file.
