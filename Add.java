//@@author A0145732H
/**
 * Add command to handle adding a task to the task list.
 * @author Katherine Coronado
 *
 */
public class Add extends Command implements Undoable {
	private static final String SUCCESS_ADD = "Added %s.";
	private static final String SUCCESS_ADD_UNDO = "\"%s\" was removed.";
	
	private Task task;
	private boolean wasExecuted;
	
	public Add(Task task) {
		this.task = task;
		this.wasExecuted = false;
	}
	
	@Override
	/**
	 * Add a task to the task list.
	 */
	public void execute() throws Exception {
		StorageManager.writeTask(task);
		wasExecuted = true;
	}

	@Override
	/**
	 * Remove the task added by this instance of Add.
	 */
	public void undo() throws Exception {
		StorageManager.removeTask(task);
	}

	@Override
	public String getSuccessMessage() {
		assert(wasExecuted);
		return String.format(SUCCESS_ADD, Ui.getPrintableTaskString(task));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Add other = (Add)obj;
		boolean isTaskEqual = (task == null && other.getTask() == null) || task.equals(other.getTask());

		return isTaskEqual && wasExecuted == other.isExecuted();
		
	}
		
	public Task getTask() {
		return this.task;
	}

	public boolean isExecuted() {
		return wasExecuted;
	}
	
	@Override
	public String getUndoMessage() {
		assert(wasExecuted);
		return String.format(SUCCESS_ADD_UNDO, task.getName());
	}
}
