/*
 *The logic class will process user input from UI, and return the result of the processing to UI.
 *For example, UI gets info about successful execution   of a command. 
 *Errors encountered during input parsing or execution will be propagated by exceptions
 *@author Dickson
 */

public class Logic {
private static boolean isStorageOpen = false; // whether storage component is ready to read and write

public static void processUserInput(String userInput) throws Exception {
	Command command = CommandParser.getCommandFromInput(userInput);
	if (isStorageOpen == false) {
		openStorage(); 
	}
	
switch(command.getCommandType()) {
case ADD:
	execAdd(command);
	break;
case EXIT:
	execExit();
	break;
case INVALID:
	throw new Exception("Invalid input.");
case REMOVE:
	execRemove(command);
	default:
		throw new Exception("Invalid input.");
}

Ui.displayCommandSuccess(command);
}

private static void openStorage() {
	StorageManager.openStorage();
	isStorageOpen = true;
}

private static void closeStorage() {
	StorageManager.closeStorage();
	isStorageOpen = false;
}

private static void execAdd(Command c) throws Exception {
	Task newTask = c.getCommandTask();
	StorageManager.writeTask(newTask);	
}

private static void execExit() {
	closeStorage();
	System.exit(0);
}

private static void execRemove(Command c) throws Exception {
	Task taskToRemove = c.getCommandTask();
	StorageManager.removeTask(taskToRemove);	
}

private static void execList(Command c) throws Exception {
	Task[] tasks = StorageManager.readAllTask();
Ui.showTasks(tasks);	
}

}
