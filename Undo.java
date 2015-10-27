import java.util.EmptyStackException;
import static org.junit.Assert.assertTrue;

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
			lastExecuted = Logic.getLastCommand();
		} catch (EmptyStackException e) {
			throw new Exception("No commands to undo.");
		}
		lastExecuted.undo();
		wasExecuted = true;
	}

	@Override
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		return lastExecuted.getUndoMessage();
	}

}
