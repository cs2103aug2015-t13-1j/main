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
	private static Stack<Undoable> undoableHistory = new Stack<Undoable>();
	private static Command lastExecutedCommand = null;
	
	public static Command processUserInput(String userInput) throws Exception {
		Command command = CommandParser.getCommandFromInput(userInput);
		
		// this may throw an Exception depending on the command
		try {
			command.execute();
		} catch (Exception e) {
			lastExecutedCommand = null;
			throw new Exception(e.getMessage());
		}
		
		// update command history depending on the command
		lastExecutedCommand = command;
		if (command.getClass() == Undo.class && !undoableHistory.isEmpty()) {
			undoableHistory.pop();
		} else if (Undoable.class.isAssignableFrom(command.getClass())) {
			undoableHistory.push((Undoable)command);
		}
		return command;
	}
	
	public static void init() {
		StorageManager.openStorage();
	}
	
	public static void close() {
		StorageManager.closeStorage();
	}
	
	/**
	 * This method searches the task list for tasks containing all of the given keywords
	 * 
	 * @param keywords	the array of keywords to search for in the task names
	 * @return			an ArrayList of the tasks containing all of the keywords
	 * 					The ArrayList will be empty if no tasks were found.
	 */
	public static ArrayList<Task> searchTasks(String[] keywords) {
		assert(keywords != null);
		
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
		return getCompletedTasks(taskList);
	}
	
	/**
	 * This method searches for all of the tasks marked as done in a given task list
	 * 
	 * @param taskList	the ArrayList to search through to get the completed tasks
	 * @return			an ArrayList of the completed tasks
	 */
	public static ArrayList<Task> getCompletedTasks(ArrayList<Task> taskList) {
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
		return getUncompletedTasks(taskList);
	}

	/**
	 * This methods searches for all of the tasks that are not marked as done in a given task list
	 * @param taskList	the ArrayList to search through to get the uncompleted tasks
	 * @return			an ArrayList of the uncompleted tasks
	 */
	public static ArrayList<Task> getUncompletedTasks(ArrayList<Task> taskList) {
		ArrayList<Task> uncompleted = new ArrayList<Task>();
		for (Task task : taskList) {
			if (!task.isDone()) {
				uncompleted.add(task);
			}
		}
		return uncompleted;
	}
	
	/**
	 * This method searches for all of the floating tasks in the entire task list
	 * @return	an ArrayList of the floating tasks
	 */
	public static ArrayList<Task> getFloatingTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		return getFloatingTasks(taskList);
	}
	
	/**
	 * This method searches for all of the floating tasks in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for floating tasks
	 * @return			an ArrayList of the found floating tasks
	 */
	public static ArrayList<Task> getFloatingTasks(ArrayList<Task> taskList) {
		ArrayList<Task> floating = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() == null && task.getEndDateTime() == null) {
				floating.add(task);
			}
		}
		return floating;	
	}
	
	/**
	 * This method searches for all of the deadline tasks in the entire task list
	 * @return	an ArrayList of the deadline tasks
	 */
	public static ArrayList<Task> getDeadlineTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		return getDeadlineTasks(taskList);
	}
	
	/**
	 * This method searches for all of the floating tasks in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for floating tasks
	 * @return			an ArrayList of the found floating tasks
	 */
	public static ArrayList<Task> getDeadlineTasks(ArrayList<Task> taskList) {
		ArrayList<Task> deadlines = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() == null && task.getEndDateTime() != null) {
				deadlines.add(task);
			}
		}
		return deadlines;	
	}
	
	/**
	 * This method searches for all of the floating tasks in the entire task list
	 * @return	an ArrayList of the floating tasks
	 */
	public static ArrayList<Task> getEvents() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		return getEvents(taskList);
	}
	
	/**
	 * This method searches for all of the floating tasks in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for floating tasks
	 * @return			an ArrayList of the found floating tasks
	 */
	public static ArrayList<Task> getEvents(ArrayList<Task> taskList) {
		ArrayList<Task> events = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() != null && task.getEndDateTime() != null) {
				events.add(task);
			}
		}
		return events;	
	}
	
	/**
	 * Get the last undoable command executed by the program.
	 * 
	 * @return	the last Undoable command stored in the command history
	 * @throws EmptyStackException	if there are no more commands in the stack
	 */
	public static Undoable getLastUndoable() throws EmptyStackException {
		return undoableHistory.peek();
	}

	private static ArrayList<Task> getDefaultTaskList() {
		return getUncompletedTasks();
	}

	/**
	 * Get the task list to correspond to subsequent references by index
	 * 
	 * @return	the list of tasks
	 */
	public static ArrayList<Task> updateCurrentTaskList() {
		if (lastExecutedCommand != null && lastExecutedCommand.getClass() == List.class) {
			List command = (List)lastExecutedCommand;
			return command.getTaskList();
		} else {
			return getDefaultTaskList();
		}
	}
	
	/**
	 * Generate the default view depending on whether a list was just shown or not
	 * 
	 * @return	a string representation of the default view to show
	 */
	public static String getDefaultView() {
		if (lastExecutedCommand != null && lastExecutedCommand.getClass() == List.class) {
			return "";
		} else {
			ArrayList<Task> taskList = Ui.getCurrentTaskList();
			return Ui.createTaskListDisplay(taskList) + "\n\n";
		}
	}
}
