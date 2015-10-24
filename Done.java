import static org.junit.Assert.assertTrue;

/**
 * Done command to mark a Task as complete. 
 * @author Dickson
 *
 */

public class Done extends Command {
	private static final String SUCCESS_DONE = "\"%s\" is now marked completed.";
	private boolean wasExecuted;
	private Task completedTask;
	public Done(Task task) {
		this.wasExecuted = false;
		completedTask = task;
	}
	
	@Override
	/**
	 * Calls the necessary methods to mark a task as completed
	 */
	public void execute() throws Exception {
		StorageManager.removeTask(completedTask);
		completedTask.setDone(true);
		StorageManager.writeTask(completedTask);
		wasExecuted = true;
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