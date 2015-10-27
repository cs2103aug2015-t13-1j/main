/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public abstract class Command {
    public abstract void execute() throws Exception;
    public abstract String getSuccessMessage();
}