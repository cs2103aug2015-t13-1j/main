import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

/**
 * Remove command to handle removing a task from the task list.
 * @author Katherine Coronado
 *
 */
public class Remove extends Command {
	private static final String SUCCESS_REMOVE = "\"%s\" was removed.";
	
	private Task task;
	private int index;
	private boolean wasExecuted;
	
	public Remove(int taskNumber) {
		this.index = taskNumber - 1;
		this.wasExecuted = false;
		this.task = null;
	}
	
	@Override
	/**
	 * Remove the task from the task list.
	 */
	public void execute() throws Exception {
		ArrayList<Task> taskList = Ui.getCurrentTaskList();
		if (index >= 0 && index < taskList.size()) {
			task = taskList.get(index);
			StorageManager.removeTask(task);
		} else {
			throw new Exception("The index specified is not valid.");
		}
		wasExecuted = true;
	}

	@Override
	/**
	 * Re-add the task that was removed by this instance of Remove.
	 */
	public void undo() throws Exception {
		StorageManager.writeTask(task);
	}

	@Override
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		return String.format(SUCCESS_REMOVE, task.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Remove other = (Remove)obj;		
		if (!this.getTask().equals(other.getTask())) {
			return false;
		} else {
			return true;
		}
	}

	public Task getTask() {
		return this.task;
	}
}
