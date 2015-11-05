import java.time.LocalDateTime;
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
	private static final int DEFAULT_VIEW_NUM_FLOATING = 3;
	private static final int DEFAULT_VIEW_NUM_DEADLINES = 6;
	private static final int DEFAULT_VIEW_NUM_EVENTS = 6;
	private static final int DEFAULT_VIEW_MAX_TASKS = 15;
	private static Stack<Undoable> undoableHistory = new Stack<Undoable>();
	private static Command lastExecutedCommand = null;
	
	public static Command processUserInput(String userInput) throws Exception {
		Command command;
		try {
			command = CommandParser.getCommandFromInput(userInput);
		} catch (Exception e) {
			lastExecutedCommand = null;
			throw new Exception(e.getMessage());			
		}
		
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

	//@@author A0145732H
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
				String taskName = currentTask.getName().toLowerCase();
				if (!taskName.contains(keywords[keywordIndex++].toLowerCase())) {
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
	
	public static ArrayList<Task> getTodaysTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		return getTodaysTasks(taskList);
	}
	
	public static ArrayList<Task> getTodaysTasks(ArrayList<Task> taskList) {
		LocalDateTime today = LocalDateTime.now();
		ArrayList<Task> todaysTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDateTime start = task.getStartDateTime();
			LocalDateTime end = task.getEndDateTime();
			if (start != null && end != null) {
				if (areDatesEqual(start, today) || areDatesEqual(end, today)) {
					todaysTasks.add(task);
				}
			} else if (end != null) {
				if (areDatesEqual(end, today)) {
					todaysTasks.add(task);
				}
			}
		}
		return todaysTasks;
	}
	
	public static ArrayList<Task> getTomorrowsTasks() {
		ArrayList<Task> taskList = StorageManager.readAllTasks();
		return getTomorrowsTasks(taskList);
	}
	
	public static ArrayList<Task> getTomorrowsTasks(ArrayList<Task> taskList) {
		LocalDateTime tomorrow = getTomorrowsDate();
		ArrayList<Task> tomorrowsTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDateTime start = task.getStartDateTime();
			LocalDateTime end = task.getEndDateTime();
			if (start != null && end != null) {
				if (areDatesEqual(start, tomorrow) || areDatesEqual(end, tomorrow)) {
					tomorrowsTasks.add(task);
				}
			} else if (end != null) {
				if (areDatesEqual(end, tomorrow)) {
					tomorrowsTasks.add(task);
				}
			}
		}
		return tomorrowsTasks;
	}
	
	public static LocalDateTime getTomorrowsDate() {
		LocalDateTime today = LocalDateTime.now();
		return today.plusDays(1);
	}
	
	public static boolean areDatesEqual(LocalDateTime date1, LocalDateTime date2) {
		boolean isDateEqual = date1.getDayOfMonth() == date2.getDayOfMonth();
		boolean isMonthEqual = date1.getMonth() == date2.getMonth();
		boolean isYearEqual = date1.getYear() == date2.getYear();
		return isDateEqual && isMonthEqual && isYearEqual;
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

	/**
	 * Generate the default task list for the default view.
	 * @return
	 */
	private static ArrayList<Task> getDefaultTaskList() {
		ArrayList<Task> defaultTasks = new ArrayList<Task>();
		ArrayList<Task> uncompleted = getUncompletedTasks();
		ArrayList<Task> uncompletedFloating = getFloatingTasks(uncompleted);
		ArrayList<Task> uncompletedDeadlines = getDeadlineTasks(uncompleted);
		ArrayList<Task> uncompletedEvents = getEvents(uncompleted);
		
		int numTasks = 0;
		int tasksForView;
		// display up to 15 tasks
		if (uncompleted.size() < DEFAULT_VIEW_MAX_TASKS) {
			tasksForView = uncompleted.size();
		} else {
			tasksForView = DEFAULT_VIEW_MAX_TASKS;
		}
		// display an even amount of each task type.
		// if there are not enough tasks of a task type, then distribute between the remaining types.
		int eventIndex = 0;
		int deadlineIndex = 0;
		int floatingIndex = 0;
		int extraTasks = 0;
		while (numTasks < tasksForView) {
			if (eventIndex < uncompletedEvents.size()) {
				if (eventIndex < DEFAULT_VIEW_NUM_EVENTS) {
					defaultTasks.add(uncompletedEvents.get(eventIndex++));
					numTasks++;				
				} else if (extraTasks > 0) {
					defaultTasks.add(uncompletedEvents.get(eventIndex++));
					numTasks++;				
					extraTasks--;
				}
				if (numTasks == tasksForView) {
					break;
				}
			} else if (eventIndex < DEFAULT_VIEW_NUM_EVENTS) {
				extraTasks++;
				eventIndex++;
			}
			if (deadlineIndex < uncompletedDeadlines.size()) {
				if (deadlineIndex < DEFAULT_VIEW_NUM_DEADLINES) {
					defaultTasks.add(uncompletedDeadlines.get(deadlineIndex++));
					numTasks++;				
				} else if (extraTasks > 0) {
					defaultTasks.add(uncompletedDeadlines.get(deadlineIndex++));
					numTasks++;				
					extraTasks--;
				}
				if (numTasks == tasksForView) {
					break;
				}
			} else if (deadlineIndex < DEFAULT_VIEW_NUM_DEADLINES) {
				extraTasks++;
				deadlineIndex++;
			}
			if (floatingIndex < uncompletedFloating.size()) {
				if (floatingIndex < DEFAULT_VIEW_NUM_FLOATING) {
					defaultTasks.add(uncompletedFloating.get(floatingIndex++));
					numTasks++;				
				} else if (extraTasks > 0) {
					defaultTasks.add(uncompletedFloating.get(floatingIndex++));
					numTasks++;				
					extraTasks--;
				}
				if (numTasks == tasksForView) {
					break;
				}
			}else if (floatingIndex < DEFAULT_VIEW_NUM_FLOATING) {
				extraTasks++;
				floatingIndex++;
			}
		}
		
		defaultTasks.sort(null);
		return defaultTasks;
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
