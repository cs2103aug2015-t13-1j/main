//@@author A0145732H
/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public abstract class Command {
	public static StorageManager storageManager = new StorageManager();
	
	public abstract void execute() throws Exception;
	public abstract String getSuccessMessage();
}