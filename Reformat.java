import java.util.ArrayList;

//@@author A0100081E
public class Reformat extends Command implements Undoable {
	private static final String SUCCESS_REFORMAT = "All tasks were erased.";
	private static final String SUCCESS_REFORMAT_UNDO = "All tasks were rewritten.";
	private ArrayList<Task> erasedTaskList;
	private boolean wasExecuted;
	
	public Reformat() {
		this.wasExecuted = false;
		this.erasedTaskList = new ArrayList<Task>();
	}
	
	@Override
	public void execute() throws Exception {
		erasedTaskList = storageManager.readAllTasks();
		storageManager.clearAllTasks();
		wasExecuted = true;
	}

	@Override
	public String getSuccessMessage() {
		return SUCCESS_REFORMAT;
	}

	//@@author A0145732H
	@Override
	public void undo() throws Exception {
		assert(wasExecuted);
		for (Task task : erasedTaskList) {
			storageManager.writeTask(task);
		}
	}

	@Override
	public String getUndoMessage() {
		return SUCCESS_REFORMAT_UNDO;
	}
}
