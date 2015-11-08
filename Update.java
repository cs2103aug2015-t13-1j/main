//@@author A0145732H
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

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
	private static final String ERROR_TASK_ALREADY_EXISTS = "Update failed because applying these changes would have caused duplicate tasks.";
	private static final String ERROR_CHANGES_DO_NOT_RESULT_IN_DIFFERENT_TASK = "Performing these changes do not result with an updated task that is different from the original.";
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

	@Override
	/**
	 * Update the task.
	 */
	public void execute() throws Exception {
		ArrayList<Task> taskList = Ui.getCurrentTaskList();
		
		if (taskIndex < 0 || taskIndex >= taskList.size()) {
			log.log(Level.INFO, "aborting, the task index " + taskIndex + " is invalid\n");
			throw new Exception(ERROR_INDEX_INVALID);
		}
	
			oldTask = taskList.get(taskIndex);
			createUpdatedTask();
			// validateDates() will throw an exception if the dates are not valid
			logic.validateDates(newTask.getStartDateTime(), newTask.getEndDateTime());
			
			if (oldTask.equals(newTask)) {
				log.log(Level.INFO, "aborting, the new task resulting from requested changes and the old task are identical\n");
				throw new Exception(ERROR_CHANGES_DO_NOT_RESULT_IN_DIFFERENT_TASK);
			}
			
			if (logic.doesTaskExist(newTask)) {
				log.log(Level.INFO, "aborting, the new task already exists\n");
				throw new Exception(ERROR_TASK_ALREADY_EXISTS);
			}
			
			storageManager.updateTask(oldTask, newTask);
		wasExecuted = true;
	}

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
				log.log(Level.WARNING, "aborting, the nameAction field was initialized to remove by commandParser, which should be impossible\n");
				assert("A request to remove task name slipped through command parser's "
						+ "defences, execution should not reach here" == null);
		}
		
		assert(newName != null);
		
		// for requests to remove non-existent fields, like removing the start date 
		// of a unscheduled task, forgive and ignore the error
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
			log.log(Level.INFO, "aborting, the new task is invalid as it has a start date but no end date\n");
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
		storageManager.updateTask(newTask, oldTask);
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
