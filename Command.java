/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public class Command {
    public enum Type {
        ADD, LIST, UPDATE, REMOVE, INVALID, EXIT
    }

    private Type type;
    private Task task = null;

    public Command(Type type) {
        this.type = type;
    }

    public Command(Type type, Task task) {
        this.type = type;
        this.task = task;
    }

    public Type getCommandType() {
        return type;
    }

    public Task getCommandTask() {
        return task;
    }
}