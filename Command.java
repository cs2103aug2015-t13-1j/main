package main.java.backend;
import java.util.ArrayList;

/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public class Command {
    
    public enum Type {
        ADD, LIST, UPDATE, REMOVE
    }

    private Type type;
    private Task task;

    public Command(Type type, Task task) {
        this.type = type;
        this.task = task;
    }
}