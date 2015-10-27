import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * List command used for listing all tasks or for performing a search on the task list
 * @author Katherine Coronado
 *
 */
public class List extends Command {
	private static final String MESSAGE_NO_TASKS = "No tasks to display.";
	private static final String MESSAGE_LIST_HEADER = "#   Start\t | End\t\t | Name\n";
	private static final String MESSAGE_FLOATING = "%d. \t\t | \t\t | %s\n";
	private static final String MESSAGE_DEADLINE = "%d. \t\t | %s\t | %s\n";
	private static final String MESSAGE_EVENT = "%d. %s\t | %s\t | %s\n";
	private static final String MESSAGE_DATE_TIME_FORMAT = "%02d %s %d:%02d";
	
	private Task task;
	private String[] keywords;
	private ArrayList<Task> taskList;
	private boolean wasExecuted;
	// TODO ArrayList<Tag> tags;
	
	// TODO constructor taking ArrayList<Task> as parameter for search refining
	
	/**
	 * Constructs a List object to search for tasks containing specific words or dates
	 * 
	 * @param task	the Task specifying search words and/or dates
	 */
	public List(Task task) {
		this.task = task;
		String keywordsList = task.getName();
		// TODO is this the best way to split the keywords? can make a parameter
		this.keywords = keywordsList.split(" ");
		this.taskList = null;
		this.wasExecuted = false;
	}
	
	/** 
	 * Constructs a List object to list all tasks
	 */
	public List() {
		this.task = null;
		this.keywords = null;
		this.taskList = null;
		this.wasExecuted = false;
	}
	
	@Override
	/**
	 * This method handles whether to search the task list or get the uncompleted tasks list. 
	 */
	public void execute() throws Exception {
		if (task != null) {
			taskList = Logic.searchTasks(keywords);
		} else {
			taskList = Logic.getUncompletedTasks();
		}
		wasExecuted = true;
	}

	@Override
	/**
	 * Prints out all the tasks in the taskList
	 */
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		if (taskList.size() > 0) {
			StringBuilder message = new StringBuilder();
			message.append(MESSAGE_LIST_HEADER);
			int taskNumber = 1;
			for (Task task : taskList) {
				LocalDateTime start = task.getStartDateTime();
				LocalDateTime end = task.getEndDateTime();
				if (end == null && start == null) {
					message.append(String.format(MESSAGE_FLOATING, taskNumber++, task.getName()));
				} else if (start == null) {
					message.append(String.format(MESSAGE_DEADLINE, taskNumber++, 
							getDateTimeFormat(end), task.getName()));
				} else {
					message.append(String.format(MESSAGE_EVENT, taskNumber++, getDateTimeFormat(start), 
							getDateTimeFormat(end), task.getName()));
				}
			}
			return message.toString();
		} else {
			return MESSAGE_NO_TASKS;
		}
	}
	
	/**
	 * This method creates a String in the format dd mmm hh:mm, i.e. 24 Oct 13:00
	 * 
	 * @param dateTime	The LocalDateTime with the date and time to format
	 * @return			a String in the format dd mmm hh:mm
	 */
	private String getDateTimeFormat(LocalDateTime dateTime) {
		String month = dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		return String.format(MESSAGE_DATE_TIME_FORMAT, dateTime.getDayOfMonth(), 
				month, dateTime.getHour(), dateTime.getMinute());
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
		// TODO check if keywords are the same?
		if (!this.getTask().equals(other.getTask())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This method returns the tasks that contain the keywords.
	 * 
	 * @return	ArrayList of tasks generated by the execute() method
	 */
	public ArrayList<Task> getTaskList() {
		// assert that this instance of List has been executed before returning
		assertNotEquals(null, taskList);
		return this.taskList;
	}
	
	public Task getTask() {
		return this.task;
	}

}
