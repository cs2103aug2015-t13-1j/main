/*
 *The logic class will process user input from UI, and return the result of the processing to UI.
 *For example, UI gets info about successful execution   of a command. 
 *Errors encountered during input parsing or execution will be propagated by exceptions
 *@author Dickson
 */

public class Logic {
	private static boolean isStorageOpen = false; // whether storage component is ready to read and write
	
	public static Command processUserInput(String userInput) throws Exception {
		Command command = CommandParser.getCommandFromInput(userInput);
		if (isStorageOpen == false) {
			openStorage(); 
		}
		command.execute();
		return command;
	}

	private static void openStorage() {
		StorageManager.openStorage();
		isStorageOpen = true;
	}
/*	
	private static void closeStorage() {
		StorageManager.closeStorage();
		isStorageOpen = false;
	}
	
	private static void execUpdate(Command command) throws Exception {
		Task updateTask = command.getCommandTask();
//		StorageManager.updateTask(updateTask);
		// TODO temp code: delete the task and then go back and re-add the new task
		StorageManager.removeTask(updateTask);
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
		Task[] tasks = StorageManager.readAllTasks();
		Ui.listTasks(tasks);	
	}
*/
}
