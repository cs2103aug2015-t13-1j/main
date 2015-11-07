//@@author A0145732H
/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public abstract class Command {
	protected static StorageManager storageManager = null;
	
	public abstract void execute() throws Exception;
	public abstract String getSuccessMessage();
	//@@author A0126270N
	public static void setStorageManager(StorageManager sm) {
		assert(sm != null);
		storageManager = sm;
	}
}