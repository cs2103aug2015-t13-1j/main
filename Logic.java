//@@author A0145732H
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/*
 *The logic class will process user input from UI, and return the result of the processing to UI.
 *For example, UI gets info about successful execution   of a command. 
 *Errors encountered during input parsing or execution will be propagated by exceptions
 */

public class Logic {
	// error messages for when the date configuration of tasks is invalid
	private static final String ERROR_DATE_INVALID_EVENT = "You cannot have an end time earlier than the start time.";
	
	// constants to define the size of the default task view and the number of each type of task
	public static final int DEFAULT_VIEW_NUM_UNSCHEDULED = 3;
	public static final int DEFAULT_VIEW_NUM_DEADLINES = 6;
	public static final int DEFAULT_VIEW_NUM_EVENTS = 6;
	public static final int DEFAULT_VIEW_MAX_TASKS = 15;
	
	// class variables to keep track of command history for the undo command
	private static Stack<Undoable> undoableHistory = new Stack<Undoable>();
	private static Command lastExecutedCommand = null;
	
	// single instances
	protected static StorageManager storageManager = null;
	protected static Logic commandLogic = null;
	
	public Logic() {
	}
	
	//@@author A0126270N
		public void init(StorageManager sm, Logic logic) throws Exception {
			assert(sm != null);
			storageManager = sm;
			storageManager.openStorage();
			Command.setStorageManager(sm);
			
			assert(logic != null);
			commandLogic = logic;
			Command.setLogic(commandLogic);
		}
		
		public void close() throws Exception {
			storageManager.closeStorage();
		}
		
	//@@author A0145732H
	public Command processUserInput(String userInput) throws Exception {
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
	
	/**
	 * This method searches the task list for tasks containing all of the given keywords
	 * 
	 * @param keywords	the array of keywords to search for in the task names
	 * @return			an ArrayList of the tasks containing all of the keywords
	 * 					The ArrayList will be empty if no tasks were found.
	 */
	public ArrayList<Task> searchTasks(String[] keywords) {
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
	public ArrayList<Task> getCompletedTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getCompletedTasks(taskList);
	}
	
	/**
	 * This method searches for all of the tasks marked as done in a given task list
	 * 
	 * @param taskList	the ArrayList to search through to get the completed tasks
	 * @return			an ArrayList of the completed tasks
	 */
	public ArrayList<Task> getCompletedTasks(ArrayList<Task> taskList) {
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
	public ArrayList<Task> getUncompletedTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getUncompletedTasks(taskList);
	}

	/**
	 * This methods searches for all of the tasks that are not marked as done in a given task list
	 * @param taskList	the ArrayList to search through to get the uncompleted tasks
	 * @return			an ArrayList of the uncompleted tasks
	 */
	public ArrayList<Task> getUncompletedTasks(ArrayList<Task> taskList) {
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
	public ArrayList<Task> getUnscheduledTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getUnscheduledTasks(taskList);
	}
	
	/**
	 * This method searches for all of the unscheduled tasks in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for unscheduled tasks
	 * @return			an ArrayList of the found unscheduled tasks
	 */
	public ArrayList<Task> getUnscheduledTasks(ArrayList<Task> taskList) {
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
	public ArrayList<Task> getDeadlineTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getDeadlineTasks(taskList);
	}
	
	/**
	 * This method searches for all of the deadlines in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for deadlines
	 * @return			an ArrayList of the found deadlines
	 */
	public ArrayList<Task> getDeadlineTasks(ArrayList<Task> taskList) {
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
	public ArrayList<Task> getEvents() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getEvents(taskList);
	}
	
	/**
	 * This method searches for all of the events in a specified task list
	 * 
	 * @param taskList	the specified task list to filter for events
	 * @return			an ArrayList of the found events
	 */
	public ArrayList<Task> getEvents(ArrayList<Task> taskList) {
		assert(taskList != null);
		ArrayList<Task> events = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.getStartDateTime() != null && task.getEndDateTime() != null) {
				events.add(task);
			}
		}
		return events;	
	}
	
	public ArrayList<Task> getTodaysTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getTodaysTasks(taskList);
	}
	
	public ArrayList<Task> getTodaysTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		LocalDate today = LocalDate.now();
		ArrayList<Task> todaysTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDate start = task.getStartDateTime().toLocalDate();
			LocalDate end = task.getEndDateTime().toLocalDate();
			if (start != null && end != null) {
				if (start.compareTo(today) == 0 || end.compareTo(today) == 0) {
					todaysTasks.add(task);
				}
			} else if (end != null) {
				if (end.compareTo(today) == 0) {
					todaysTasks.add(task);
				}
			}
		}
		return todaysTasks;
	}
	
	public ArrayList<Task> getTomorrowsTasks() {
		ArrayList<Task> taskList = storageManager.readAllTasks();
		return getTomorrowsTasks(taskList);
	}
	
	public ArrayList<Task> getTomorrowsTasks(ArrayList<Task> taskList) {
		assert(taskList != null);
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		ArrayList<Task> tomorrowsTasks = new ArrayList<Task>();
		for (Task task : taskList) {
			LocalDate start = task.getStartDateTime().toLocalDate();
			LocalDate end = task.getEndDateTime().toLocalDate();
			if (start != null && end != null) {
				if (start.compareTo(tomorrow) == 0 || end.compareTo(tomorrow) == 0) {
					tomorrowsTasks.add(task);
				}
			} else if (end != null) {
				if (end.compareTo(tomorrow) == 0) {
					tomorrowsTasks.add(task);
				}
			}
		}
		return tomorrowsTasks;
	}
	
	public void validateDates(LocalDateTime start, LocalDateTime end) throws Exception {
		if (start == null) {
			// dates are always valid for unscheduled tasks and deadlines
			return;
		}
		boolean areDatesValid = end.compareTo(start) > 0;
		if (!areDatesValid) {
			throw new Exception(ERROR_DATE_INVALID_EVENT);
		}
	}
	
	/**
	 * Get the last undoable command executed by the program.
	 * 
	 * @return	the last Undoable command stored in the command history
	 * @throws EmptyStackException	if there are no more commands in the stack
	 */
	public Undoable getLastUndoable() throws EmptyStackException {
		return undoableHistory.peek();
	}

	/**
	 * Generate the default task list for the default view.
	 * @return
	 */
	private ArrayList<Task> getDefaultTaskList() {
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
	public ArrayList<Task> updateCurrentTaskList() {
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
	public String getDefaultView() {
		if (lastExecutedCommand != null && lastExecutedCommand.getClass() == List.class) {
			return "";
		} else {
			ArrayList<Task> taskList = Ui.getCurrentTaskList();
			return Ui.createTaskListDisplay(taskList) + "\n\n";
		}
	}

	//@@author A0126270N
	/*
	 *Determines if the specified task already exists. This is used to prevent adding or updating which would cause duplicate tasks
	 */
	public boolean doesTaskExist(Task task) {
		assert(task != null);
		ArrayList<Task> taskList = storageManager.readAllTasks();
		
		for (Task t : taskList) {
			if (t.equals(task)) {
				return true;
			}
		}
		
		return false;
	}
}
