import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * List command used for listing all tasks or for performing a search on the task list
 * @author Katherine Coronado
 *
 */
public class List extends Command {
	private static final String MESSAGE_NO_TASKS = "No tasks to display.";
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
	public void execute() throws Exception {
		if (task != null) {
			taskList = StorageManager.searchTasks(keywords);
		} else {
			taskList = new ArrayList<Task>();
			Task[] taskArray = StorageManager.readAllTasks();
			taskList.addAll(Arrays.asList(taskArray));
		}
		wasExecuted = true;
	}

	@Override
	public void undo() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	/**
	 * Prints out all the tasks in the taskList
	 */
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		if (taskList.size() > 0) {
			StringBuilder message = new StringBuilder();
			int taskNumber = 1;
			for (Task task : taskList) {
				message.append((taskNumber++) + ". " + task.getName() + "\n");
			}
			// TODO format for tasks with dates
			return message.toString();
		} else {
			return MESSAGE_NO_TASKS;
		}
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
	 * If the command has not been executed yet, the method will execute the command 
	 * prior to returning the taskList.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Task> getTaskList() throws Exception {
		if (taskList == null) {
			this.execute();
		}
		return this.taskList;
	}
	
	public Task getTask() {
		return this.task;
	}

}
