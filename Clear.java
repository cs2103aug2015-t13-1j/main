//@@author A0100081E
import java.util.ArrayList;

public class Clear extends Command implements Undoable {
	private static final String SUCCESS_CLEAR = "All tasks were erased.";
	private static final String SUCCESS_CLEAR_UNDO = "All tasks were rewritten.";
	private ArrayList<Task> erasedTaskList;
	private boolean wasExecuted;
	
	public Clear() {
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
		return SUCCESS_CLEAR;
	}

	//@@author A0145732H
	@Override
	public void undo() throws Exception {
		assert(wasExecuted);
		for (Task task : erasedTaskList) {
			storageManager.writeTask(task);
		}
	}

	//@@author A0100081E
	@Override
	public String getUndoMessage() {
		return SUCCESS_CLEAR_UNDO;
	}
}
