//@@author A0145732H
import java.util.ArrayList;

/**
 * Remove command to handle removing a task from the task list.
 * @author Katherine Coronado
 *
 */
public class Remove extends Command implements Undoable {
	private static final String SUCCESS_REMOVE = "\"%s\" was removed.";
	private static final String SUCCESS_REMOVE_UNDO = "\"%s\" was re-added.";
	private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
	
	private Task task;
	private int index;
	private boolean isExecuted;
	
	public Remove(int taskNumber) {
		this.index = taskNumber - 1;
		this.isExecuted = false;
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
			throw new Exception(ERROR_INDEX_INVALID);
		}
		isExecuted = true;
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
		assert(isExecuted);
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
		boolean isTaskEqual = (task == null && other.getTask() == null) || (task != null && task.equals(other.getTask()));
		
		return isTaskEqual && isExecuted == other.isExecuted() && index == other.getTaskIndex();
		}
	
		public Task getTask() {
		return this.task;
	}

	public int getTaskIndex() {
		return index;
	}
	
	public boolean isExecuted() {
		return isExecuted;
	}
	
	@Override
	public String getUndoMessage() {
		assert(isExecuted);
		return String.format(SUCCESS_REMOVE_UNDO, task.getName());
	}
}
