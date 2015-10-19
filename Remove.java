import static org.junit.Assert.assertTrue;

public class Remove extends Command {
	private static final String SUCCESS_REMOVE = "\"%s\" was removed.";
	
	private Task task;
	private boolean wasExecuted;
	
	public Remove(Task task) {
		this.task = task;
		this.wasExecuted = false;
	}
	
	@Override
	public void execute() throws Exception {
		StorageManager.removeTask(this.task);
		wasExecuted = true;
	}

	@Override
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
