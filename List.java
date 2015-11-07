//@@author A0145732H
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * List command used for listing all tasks or for performing a search on the task list
 * @author Katherine Coronado
 *
 */
public class List extends Command {	
	private String[] keywords;
	private ArrayList<Task> taskList;
	EnumSet<LIST_FLAGS> flags;
	private boolean wasExecuted;
	
	public enum LIST_FLAGS {
		UNSCHEDULED, DEADLINE, EVENT, COMPLETED, UNCOMPLETED, TODAY, TOMORROW;
	}
	public static final EnumSet<LIST_FLAGS> LIST_FLAGS_ENUM_SET = EnumSet.allOf(LIST_FLAGS.class);
	
	/** 
	 * Constructs a List object to list all tasks
	 */
	public List() {
		this.keywords = null;
		this.taskList = null;
		this.flags = null;
		this.wasExecuted = false;
	}
	
	public List(EnumSet<LIST_FLAGS> listFlags) {
		this();
		this.flags = listFlags;
	}

	public List(EnumSet<LIST_FLAGS> listFlags, String[] keywords) {
		this(listFlags);
		this.keywords = keywords;
	}

	@Override
	/**
	 * This method handles whether to filter the task list or get the uncompleted tasks list. 
	 */
	public void execute() throws Exception {
		if (keywords != null || flags != null) {
			ArrayList<Task> tasks = storageManager.readAllTasks();
			if (keywords != null) {
				tasks = logic.searchTasks(keywords);
			}
			if (flags != null) {
				tasks = getFlaggedTasks(tasks);
			}
			taskList = tasks;
		} else {
			taskList = logic.getUncompletedTasks();
		}
		wasExecuted = true;
	}

	private ArrayList<Task> getFlaggedTasks(ArrayList<Task> flaggedTasks) {
		// keep refining the task list based on which flags are marked
		if (flags.contains(LIST_FLAGS.COMPLETED)) {
			flaggedTasks = logic.getCompletedTasks(flaggedTasks);
		}
		if (flags.contains(LIST_FLAGS.UNCOMPLETED)) {
			flaggedTasks = logic.getUncompletedTasks(flaggedTasks);
		}		
		if (flags.contains(LIST_FLAGS.UNSCHEDULED)) {
			flaggedTasks = logic.getUnscheduledTasks(flaggedTasks);
		}
		if (flags.contains(LIST_FLAGS.DEADLINE)) {
			flaggedTasks = logic.getDeadlineTasks(flaggedTasks);
		}
		if (flags.contains(LIST_FLAGS.EVENT)) {
			flaggedTasks = logic.getEvents(flaggedTasks);
		}
		if (flags.contains(LIST_FLAGS.TODAY)) {
			flaggedTasks = logic.getTodaysTasks(flaggedTasks);
		}
		if (flags.contains(LIST_FLAGS.TOMORROW)) {
			flaggedTasks = logic.getTomorrowsTasks(flaggedTasks);
		}
		return flaggedTasks;
	}

	@Override
	/**
	 * Prints out all the tasks in the taskList
	 */
	public String getSuccessMessage() {
		assert(wasExecuted);
		return Ui.createTaskListDisplay(taskList);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		List other = (List)obj;	
		boolean isExecutedEqual = (this.wasExecuted == other.wasExecuted);
		boolean areKeywordsEqual = Arrays.equals(this.keywords, other.keywords);
		boolean areTaskListsEqual = this.taskList.equals(other.taskList);
		boolean areFlagsEqual = this.flags.equals(other.flags);
		return (isExecutedEqual && areKeywordsEqual && areTaskListsEqual && areFlagsEqual);
	}
	
	/**
	 * This method returns the tasks that contain the keywords.
	 * 
	 * @return	ArrayList of tasks generated by the execute() method
	 */
	public ArrayList<Task> getTaskList() {
		// assert that this instance of List has been executed before returning
		assert(taskList != null);
		return this.taskList;
	}
}
