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
	execExit(command);
	break;
case INVALID:
	throw new Exception("Invalid input.");
case LIST:
	execList(command);
	break;
case REMOVE:
	execRemove(command);
	break;
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

private static void execAdd(Command command) throws Exception {
	Task newTask = command.getCommandTask();
	StorageManager.writeTask(newTask);	
}

private static void execExit(Command command) {
	closeStorage();
	Ui.displayCommandSuccess(command);
	System.exit(0);
}

private static void execRemove(Command command) throws Exception {
	Task taskToRemove = command.getCommandTask();
	StorageManager.removeTask(taskToRemove);	
}

private static void execList(Command command) throws Exception {
	Task[] tasks = StorageManager.readAllTask();
Ui.listTasks(tasks);	
}

}
