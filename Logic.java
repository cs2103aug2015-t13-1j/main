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
	// error messages for when the date configuration of tasks is invalid
	private static final String ERROR_DATE_INVALID_EVENT = "You cannot have an end time earlier than the start time or the current time.";
	private static final String ERROR_DATE_INVALID_DEADLINE = "You cannot set a deadline for earlier than the current time.";
	
	// constants to define the size of the default task view and the number of each type of task
	private static final int DEFAULT_VIEW_NUM_UNSCHEDULED = 3;
	private static final int DEFAULT_VIEW_NUM_DEADLINES = 6;
	private static final int DEFAULT_VIEW_NUM_EVENTS = 6;
	private static final int DEFAULT_VIEW_MAX_TASKS = 15;
	
	// class variables to keep track of command history for the undo command
	private static Stack<Undoable> undoableHistory = new Stack<Undoable>();
	private static Command lastExecutedCommand = null;
	
	// single instances
	private static StorageManager storageManager = new StorageManager();
	
	/**
	 * set the class variable storageManager, for dependency injection
	 * @param sm
	 */
	public static void setStorageManager(StorageManager sm) {
		storageManager = sm;
	}
	
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
		storageManager.openStorage();
	}
	
	public static void close() {
		storageManager.closeStorage();
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
		
		ArrayList<Task> taskList = storageManager.readAllTasks();
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
		ArrayList<Task> taskList = storageManager.readAllTasks();
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
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getUncompletedTasks(taskList);
	}

	/**
	 * This methods searches for all of the tasks that are not marked as done in a given task list
	 * @param taskList	the ArrayList to search through to get the uncompleted tasks
	 * @return			an ArrayList of the uncompleted tasks
	 */
	public static ArrayList<Task> getUncompletedTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		ArrayList<Task> uncompleted = new ArrayList<Task>();
		for (Task task : taskList) {
			if (!task.isDone()) {
				uncompleted.add(task);
			}
		}
		return uncompleted;
	}
	
	/**
	 * This method searches for all of the unscheduled tasks in the entire task list
	 * @return	an ArrayList of the unscheduled tasks
	 */
	public static ArrayList<Task> getUnscheduledTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getUnscheduledTasks(taskList);
	}
	
	/**
	 * This method searches for all of the unscheduled tasks in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for unscheduled tasks
	 * @return			an ArrayList of the found unscheduled tasks
	 */
	public static ArrayList<Task> getUnscheduledTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		ArrayList<Task> unscheduled = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() == null && task.getEndDateTime() == null) {
				unscheduled.add(task);
			}
		}
		return unscheduled;	
	}
	
	/**
	 * This method searches for all of the deadline tasks in the entire task list
	 * @return	an ArrayList of the deadline tasks
	 */
	public static ArrayList<Task> getDeadlineTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getDeadlineTasks(taskList);
	}
	
	/**
	 * This method searches for all of the deadlines in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for deadlines
	 * @return			an ArrayList of the found deadlines
	 */
	public static ArrayList<Task> getDeadlineTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		ArrayList<Task> deadlines = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() == null && task.getEndDateTime() != null) {
				deadlines.add(task);
			}
		}
		return deadlines;	
	}
	
	/**
	 * This method searches for all of the events in the entire task list
	 * @return	an ArrayList of the events
	 */
	public static ArrayList<Task> getEvents() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getEvents(taskList);
	}
	
	/**
	 * This method searches for all of the events in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for events
	 * @return			an ArrayList of the found events
	 */
	public static ArrayList<Task> getEvents(ArrayList<Task> taskList) {
		assert(taskList != null);
		ArrayList<Task> events = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() != null && task.getEndDateTime() != null) {
				events.add(task);
			}
		}
		return events;	
	}
	
	public static ArrayList<Task> getTodaysTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getTodaysTasks(taskList);
	}
	
	public static ArrayList<Task> getTodaysTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		LocalDateTime today = LocalDateTime.now();
		ArrayList<Task> todaysTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDateTime start = task.getStartDateTime();
			LocalDateTime end = task.getEndDateTime();
			if (start != null && end != null) {
				if (compareDates(start, today) == 0 || compareDates(end, today) == 0) {
					todaysTasks.add(task);
				}
			} else if (end != null) {
				if (compareDates(end, today) == 0) {
					todaysTasks.add(task);
				}
			}
		}
		return todaysTasks;
	}
	
	public static ArrayList<Task> getTomorrowsTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getTomorrowsTasks(taskList);
	}
	
	public static ArrayList<Task> getTomorrowsTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		LocalDateTime tomorrow = getTomorrowsDate();
		ArrayList<Task> tomorrowsTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDateTime start = task.getStartDateTime();
			LocalDateTime end = task.getEndDateTime();
			if (start != null && end != null) {
				if (compareDates(start, tomorrow) == 0 || compareDates(end, tomorrow) == 0) {
					tomorrowsTasks.add(task);
				}
			} else if (end != null) {
				if (compareDates(end, tomorrow) == 0) {
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
	
	public static int compareDates(LocalDateTime date1, LocalDateTime date2) {
		if (date1.getYear() < date2.getYear()) {
			return -1;
		} else if (date1.getYear() > date2.getYear()) {
			return 1;
		} else {
			if (date1.getDayOfYear() < date2.getDayOfYear()) {
				return -1;
			} else if (date1.getDayOfYear() > date2.getDayOfYear()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	public static void validateDates(LocalDateTime start, LocalDateTime end) throws Exception {
		boolean areDatesValid = areDatesValid(start, end);
		if (!areDatesValid) {
			if (start == null) {
				throw new Exception(ERROR_DATE_INVALID_DEADLINE);
			} else {
				throw new Exception(ERROR_DATE_INVALID_EVENT);
			}
		}
	}
	
	private static boolean areDatesValid(LocalDateTime start, LocalDateTime end) {
		// subtract a minute to account for the auto setting of our seconds to 0
		LocalDateTime currentTime = LocalDateTime.now().minusMinutes(1);
		
		if (start == null && end == null) {
			// always return true for unscheduled tasks
			return true;
		} else if (end != null) {
			boolean isEndAfterCurrent = end.compareTo(currentTime) > 0;
			if (start == null) {
				return isEndAfterCurrent;
			} else {
				boolean isEndAfterStart = end.compareTo(start) > 0;
				return (isEndAfterCurrent && isEndAfterStart);
			}
		} else {
			return false;
		}
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
		ArrayList<Task> uncompletedUnscheduled = getUnscheduledTasks(uncompleted);
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
		int unscheduledIndex = 0;
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
			if (unscheduledIndex < uncompletedUnscheduled.size()) {
				if (unscheduledIndex < DEFAULT_VIEW_NUM_UNSCHEDULED) {
					defaultTasks.add(uncompletedUnscheduled.get(unscheduledIndex++));
					numTasks++;				
				} else if (extraTasks > 0) {
					defaultTasks.add(uncompletedUnscheduled.get(unscheduledIndex++));
					numTasks++;				
					extraTasks--;
				}
				if (numTasks == tasksForView) {
					break;
				}
			}else if (unscheduledIndex < DEFAULT_VIEW_NUM_UNSCHEDULED) {
				extraTasks++;
				unscheduledIndex++;
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
