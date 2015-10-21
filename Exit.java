import static org.junit.Assert.assertTrue;

/**
 * Exit command to handle closing TaskBuddy cleanly. 
 * @author Katherine Coronado
 *
 */
public class Exit extends Command {
	private boolean wasExecuted;
	
	public Exit() {
		this.wasExecuted = false;
	}
	
	@Override
	/**
	 * Calls the necessary methods to exit TaskBuddy cleanly
	 */
	public void execute() throws Exception {
		// TODO which methods to change to public, etc.
		StorageManager.closeStorage();
		Ui.indicateExit();
		wasExecuted = true;
	}

	@Override
	public void undo() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSuccessMessage() {
		assertTrue(wasExecuted);
		return "Goodbye\n";
	}

}