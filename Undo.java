import java.util.EmptyStackException;

/**
 * Undo command to handle undoing the last command that made changes to the task list.
 * @author Katherine Coronado
 *
 */

public class Undo extends Command {
	boolean wasExecuted;
	Undoable lastExecuted;
	
	public Undo() {
		this.wasExecuted = false;
		this.lastExecuted = null;
	}
	
	@Override
	public void execute() throws Exception {
		try {
			lastExecuted = Logic.getLastUndoable();
		} catch (EmptyStackException e) {
			throw new Exception("No commands to undo.");
		}
		lastExecuted.undo();
		wasExecuted = true;
	}

	@Override
	public String getSuccessMessage() {
		assert(wasExecuted);
		return lastExecuted.getUndoMessage();
	}

	public Undoable getLastExecutedCommand() {
		return lastExecuted;
	}
	
	public boolean isExecuted() {
		return wasExecuted;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Undo other = (Undo)obj;
boolean isCommandEqual = false;
		
		if ((lastExecuted == null && other.getLastExecutedCommand() == null) || lastExecuted.equals(other.getLastExecutedCommand())) {
			isCommandEqual = true;
		}
		
		return isCommandEqual && wasExecuted == other.isExecuted();
	}
}
