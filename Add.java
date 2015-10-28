/**
 * Add command to handle adding a task to the task list.
 * @author Katherine Coronado
 *
 */
public class Add extends Command implements Undoable {
	private static final String SUCCESS_ADD = "\"%s\" was added.";
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
		return String.format(SUCCESS_ADD, task.getName());
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
		if (!this.getTask().equals(other.getTask())) {
			return false;
		} else {
			return true;
		}
	}
		
	public Task getTask() {
		return this.task;
	}

	@Override
	public String getUndoMessage() {
		assert(wasExecuted);
		return String.format(SUCCESS_ADD_UNDO, task.getName());
	}
}
