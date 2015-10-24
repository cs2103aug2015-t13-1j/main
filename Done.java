import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

/**
 * Done command to mark a Task as complete. 
 * @author Dickson
 *
 */

public class Done extends Command {
	private static final String SUCCESS_DONE = "\"%s\" is now marked completed.";
	private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
	private boolean wasExecuted;
	private Task completedTask;
	private int taskIndex; // 0-based indexing
	
	public Done(int taskNum) {
		this.wasExecuted = false;
		completedTask = null;
		taskIndex = taskNum - 1;
	}
	
	@Override
	/**
	 * Calls the necessary methods to mark a task as completed
	 */
	public void execute() throws Exception {
		completedTask = getTaskFromList();
		StorageManager.removeTask(completedTask);
		completedTask.setDone(true);
		StorageManager.writeTask(completedTask);
		wasExecuted = true;
	}

	private Task getTaskFromList() throws Exception {
		ArrayList<Task> taskList = Ui.getCurrentTaskList();
		if (taskIndex >= 0 && taskIndex < taskList.size()) {
			return taskList.get(taskIndex);
		} else {
			throw new Exception(ERROR_INDEX_INVALID);
		}
		
	}
	
	@Override
	public void undo() throws Exception {
		StorageManager.removeTask(completedTask);
		completedTask.setDone(false);
		StorageManager.writeTask(completedTask);
	}

	@Override
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		return String.format(SUCCESS_DONE, completedTask.getName());
	}
	
	public Task getTask() {
		return completedTask;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Done other = (Done)obj;		
		return this.getTask().equals(other.getTask());
				}
	}