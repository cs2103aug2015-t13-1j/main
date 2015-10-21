import static org.junit.Assert.assertTrue;

/**
 * Update command to handle updating the fields of a task.
 * @author Katherine Coronado
 *
 */

public class Update extends Command {
	private static final String SUCCESS_UPDATE = "\"%s\" was updated to \"%s\".";
	
	private Task oldTask;
	private Task newTask;
	private boolean wasExecuted;
	
	public Update(Task oldTask, Task newTask) {
		this.oldTask = oldTask;
		this.newTask = newTask;
		this.wasExecuted = false;
	}

	@Override
	/**
	 * Update the task.
	 */
	public void execute() throws Exception {
		StorageManager.updateTask(oldTask, newTask);
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
