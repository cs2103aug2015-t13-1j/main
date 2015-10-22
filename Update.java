import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

/**
 * Update command to handle updating the fields of a task.
 * @author Katherine Coronado
 *
 */

public class Update extends Command {
	private static final String SUCCESS_UPDATE = "\"%s\" was updated to \"%s\".";
	private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
	
	private Task oldTask;
	private Task newTask;
	private int index;
	private boolean wasExecuted;
	
	public Update(int taskNumber, Task newTask) {
		this.oldTask = null;
		this.newTask = newTask;
		this.index = taskNumber - 1;
		this.wasExecuted = false;
	}

	@Override
	/**
	 * Update the task.
	 */
	public void execute() throws Exception {
		ArrayList<Task> taskList = Ui.getCurrentTaskList();
		if (index >= 0 && index < taskList.size()) {
			oldTask = taskList.get(index);
			StorageManager.updateTask(oldTask, newTask);
		} else {
			throw new Exception(ERROR_INDEX_INVALID);
		}
		wasExecuted = true;
	}

	@Override
	/**
	 * Restore the task to the old state prior to executing the update command.
	 */
	public void undo() throws Exception {
		StorageManager.updateTask(newTask, oldTask);
	}

	@Override
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		// TODO check which fields were modified and display only those fields in message
		return String.format(SUCCESS_UPDATE, oldTask.getName(), newTask.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Update other = (Update)obj;		
		if (!this.getOldTask().equals(other.getOldTask())) {
			return false;
		} else if (!this.getNewTask().equals(other.getNewTask())) {
			return false;
		} else {
			return true;
		}
	}
	
	public Task getOldTask() {
		return this.oldTask;
	}
	
	public Task getNewTask() {
		return this.newTask;
	}
}
