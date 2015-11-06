//@@author A0126270N
import java.util.ArrayList;

/**
 * Done command to mark a Task as complete. 
 * @author Dickson
 *
 */

public class Done extends Command implements Undoable {
	private static final String SUCCESS_DONE = "\"%s\" is now marked completed.";
	private static final String SUCCESS_DONE_UNDO = "\"%s\" is now marked as uncompleted.";
	private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
	private boolean isExecuted;
	private Task completedTask;
	private int taskIndex; // 0-based indexing
	
	public Done(int taskNum) {
		this.isExecuted = false;
		completedTask = null;
		taskIndex = taskNum - 1;
	}
	
	@Override
	/**
	 * Calls the necessary methods to mark a task as completed
	 */
	public void execute() throws Exception {
		completedTask = getTaskFromList();
		storageManager.removeTask(completedTask);
		completedTask.setDone(true);
		storageManager.writeTask(completedTask);
		isExecuted = true;
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
		storageManager.removeTask(completedTask);
		completedTask.setDone(false);
		storageManager.writeTask(completedTask);
	}

	@Override
	public String getSuccessMessage() {
		assert(isExecuted);
		return String.format(SUCCESS_DONE, completedTask.getName());
	}
	
	public Task getTask() {
		return completedTask;
	}
	
	public boolean isExecuted() {
		return isExecuted;
	}
	
	public int getTaskIndex() {
		return taskIndex;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Done other = (Done)obj;		

		boolean isTaskEqual = (completedTask == null && other.getTask() == null) || (completedTask != null && completedTask.equals(other.getTask()));
		
		return isTaskEqual && isExecuted == other.isExecuted() && taskIndex == other.getTaskIndex();
				}

	@Override
	public String getUndoMessage() {
		assert(isExecuted);
		return String.format(SUCCESS_DONE_UNDO, completedTask.getName());
	}
}