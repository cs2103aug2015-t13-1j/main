import java.util.logging.Logger;

//@@author A0145732H
/**
 * Command is a class that contains all the required information for Logic to
 * execute it. It is created by CommandParser's parse method.
 */
public abstract class Command {
	protected static StorageManager storageManager = null;
	protected static Logic logic = null;
	protected static final Logger log = Logger.getLogger(Ui.LOG_NAME);	
	
	public abstract void execute() throws Exception;
	public abstract String getSuccessMessage();
	
	public static void setLogic(Logic logicInstance) {
		assert(logicInstance != null);
		logic = logicInstance;
	}
	
	//@@author A0126270N
	public static void setStorageManager(StorageManager sm) {
		assert(sm != null);
		storageManager = sm;
	}
}