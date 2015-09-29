/*
 *The logic class will process user input from UI, and return the result of the processing to UI.
 *For example, successful execution   of a command, or errors encountered during input parsing or execution.
 *@author Dickson
 */

public class Logic {
private static StorageManager storage = new StorageManager();
private static boolean isStorageOpen = false; // whether storage component is ready to read and write

public static void processUserInput(String userInput) throws Exception {
	Command c = CommandParser.getCommandFromInput(userInput);
	if (isStorageOpen == false) {
		openStorage(); 
	}
	
	if (c.getCommandType() == Command.Type.INVALID) {
		throw new Exception("Invalid input.");
	}
	
switch(c.getCommandType()) {
case ADD:
	execAdd(c);

}
}

private static void openStorage() {
	storage.openStorage();
	isStorageOpen = true;
}

private static void closeStorage() {
	storage.closeStorage();
	isStorageOpen = false;
}

private static void execAdd(Command c) throws Exception {
	Task newTask = c.getCommandTask();
storage.writeTask(newTask);	
}

}
