import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/*
 *The logic class will process user input from UI, and return the result of the processing to UI.
 *For example, UI gets info about successful execution   of a command. 
 *Errors encountered during input parsing or execution will be propagated by exceptions
 *@author Dickson
 */

public class Logic {
	private static boolean isStorageOpen = false; // whether storage component is ready to read and write
	private static Stack<Undoable> commandHistory = new Stack<Undoable>();
	
	public static Command processUserInput(String userInput) throws Exception {
		Command command = CommandParser.getCommandFromInput(userInput);
		if (isStorageOpen == false) {
			openStorage(); 
		}
		// this may throw an Exception depending on the command
		command.execute();
		
		// update command history depending on the command
		if (command.getClass() == Undo.class && !commandHistory.isEmpty()) {
			commandHistory.pop();
		} else if (Undoable.class.isAssignableFrom(command.getClass())) {
			commandHistory.push((Undoable)command);
		}
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
	
	/**
	 * This method searches for all of the tasks marked as done by the user.
	 * 
	 * @return	an ArrayList of tasks marked as done
	 */
	public static ArrayList<Task> getCompletedTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		ArrayList<Task> completed = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.isDone()) {
				completed.add(task);
			}
		}
		return completed;
	}
	
	/**
	 * This method searches for all of the tasks that are not marked as done by the user.
	 * 
	 * @return	an ArrayList of tasks marked as not done
	 */
	public static ArrayList<Task> getUncompletedTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		ArrayList<Task> uncompleted = new ArrayList<Task>();
		for (Task task : taskList) {
			if (!task.isDone()) {
				uncompleted.add(task);
			}
		}
		return uncompleted;
	}
	
	/**
	 * Get the last undoable command executed by the program.
	 * 
	 * @return	the last Undoable command stored in the command history
	 * @throws EmptyStackException	if there are no more commands in the stack
	 */
	public static Undoable getLastCommand() throws EmptyStackException {
		return commandHistory.peek();
	}
/*	
	private static void closeStorage() {
		StorageManager.closeStorage();
		isStorageOpen = false;
	}
*/
}
