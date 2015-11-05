//@@author A0145732H
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Update command to handle updating the fields of a task.
 * @author Katherine Coronado
 *
 */

public class Update extends Command implements Undoable {
	private static final String SUCCESS_UPDATE = "\"%s\" updated to %s.";
	private static final String SUCCESS_UPDATE_UNDO = "Update of \"%s\" undone to %s";
	private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
	private static final String ERROR_UPDATED_TASK_IS_INVALID = "The update failed because performing these changes would have resulted in an invalid task.";	
	private Task oldTask;
	private Task newTask;
	private DeltaTask changes;
	private int taskIndex;
	private boolean wasExecuted;

	//@@author A0126270N
	public Update(int taskNumber, DeltaTask changes) {
		this.oldTask = null;
		this.newTask = null;
		this.changes = changes;
		this.taskIndex = taskNumber - 1;
		this.wasExecuted = false;
	}

	//@@author A0145732H
	@Override
	/**
	 * Update the task.
	 */
	public void execute() throws Exception {
		ArrayList<Task> taskList = Ui.getCurrentTaskList();
		if (taskIndex >= 0 && taskIndex < taskList.size()) {
			oldTask = taskList.get(taskIndex);
			createUpdatedTask();
			// validateDates() will throw an exception if the dates are not valid
			Logic.validateDates(newTask.getStartDateTime(), newTask.getEndDateTime());
			StorageManager.updateTask(oldTask, newTask);
		} else {
			throw new Exception(ERROR_INDEX_INVALID);
		}
		wasExecuted = true;
	}

	//@@author A0126270N
	private void createUpdatedTask() throws Exception {
		String newName = null;
		
		switch(changes.getNameAction()) {
			case UPDATE :
				newName = changes.getNewName();
				break;
				
			case NONE :
				newName = oldTask.getName();
				break;
				
			case REMOVE :
				// command parser should not allow name's action to be initialized to REMOVE
				assert("A request to remove task name slipped through command parser's "
						+ "defences, execution should not reach here" == null);
		}
		
		assert(newName != null);
		
		// for requests to remove non-existent fields, like removing the start date 
		// off a floating task, forgive and ignore the error
		LocalDateTime newStart = null;
		switch(changes.getStartAction()) {
			case UPDATE :
				newStart = changes.getNewStart();
				break;
				
			case NONE :
				newStart = oldTask.getStartDateTime();
				break;
				
			case REMOVE :
				newStart = null;
		}
	
		LocalDateTime newEnd = null;
		switch(changes.getEndAction()) {
			case UPDATE :
				newEnd = changes.getNewEnd();
				break;
				
			case NONE :
				newEnd = oldTask.getEndDateTime();
				break;
				
			case REMOVE :
				newEnd = null;
		}
	
		
		if (isTaskParametersValid(newName, newStart, newEnd) == false) {
			throw new Exception(ERROR_UPDATED_TASK_IS_INVALID);
		}
		
		newTask = new Task(newName, newStart, newEnd, oldTask.isDone());
	}
	
	private static boolean isTaskParametersValid(String name, LocalDateTime start, LocalDateTime end) {
		assert(name != null);
		return !(start != null && end == null);
	}
	
	//@@author A0145732H
	@Override
	/**
	 * Restore the task to the old state prior to executing the update command.
	 */
	public void undo() throws Exception {
		StorageManager.updateTask(newTask, oldTask);
	}

	@Override
	public String getSuccessMessage() {
		assert(wasExecuted);
		return String.format(SUCCESS_UPDATE, oldTask.getName(), Ui.getPrintableTaskString(newTask));
	}
	
	//@@author A0126270N
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Update other = (Update)obj;
		boolean isOldTaskEqual = (oldTask == null && other.getOldTask() == null) || (oldTask != null && oldTask.equals(other.getOldTask()));
		boolean isNewTaskEqual = (newTask == null && other.getNewTask() == null) || (newTask != null && newTask.equals(other.getNewTask()));
		boolean isChangesEqual = (changes == null && other.getChanges() == null) || (changes != null && oldTask.equals(other.getChanges()));
		
		return isOldTaskEqual && isNewTaskEqual && isChangesEqual && wasExecuted == other.isExecuted() && taskIndex == other.getTaskIndex();
	}
	
	public Task getOldTask() {
		return this.oldTask;
	}
	
	public Task getNewTask() {
		return this.newTask;
	}

	// this is to facilitate unit testing
	public DeltaTask getChanges() {
		return changes;
	}
	
	public boolean isExecuted() {
		return wasExecuted;
	}

	public int getTaskIndex() {
		return taskIndex;
	}
	@Override
	public String getUndoMessage() {
		return String.format(SUCCESS_UPDATE_UNDO, newTask.getName(), Ui.getPrintableTaskString(oldTask));
	}
	
	}
