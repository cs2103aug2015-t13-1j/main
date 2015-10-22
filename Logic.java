import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

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
	
	/**
	 * This method searches the task list for tasks containing all of the given keywords
	 * 
	 * @param keywords	the array of keywords to search for in the task names
	 * @return			an ArrayList of the tasks containing all of the keywords
	 * 					The ArrayList will be empty if no tasks were found.
	 */
	public static ArrayList<Task> searchTasks(String[] keywords) {
		assertNotEquals(null, keywords);
		
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		ArrayList<Task> foundTasks = new ArrayList<Task>();		
		for (int i = 0; i < taskList.size(); i++) {
			int keywordIndex = 0;
			Task currentTask = taskList.get(i);
			
			// check if currentTask contains all of the keywords before adding to foundTasks
			while (keywordIndex < keywords.length) {
				if (!currentTask.getName().contains(keywords[keywordIndex++])) {
					break;
				}
				if (keywordIndex == keywords.length) {
					foundTasks.add(currentTask);
				}
			}
		}
		
		return foundTasks;
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
