//@@author A0145732H
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
		storageManager.closeStorage();
		Ui.indicateExit();
		wasExecuted = true;
	}

	@Override
	public String getSuccessMessage() {
		assert(wasExecuted);
		return "Goodbye\n";
	}

}
