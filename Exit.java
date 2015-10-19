import static org.junit.Assert.assertTrue;

public class Exit extends Command {
	private boolean wasExecuted;
	
	public Exit() {
		this.wasExecuted = false;
	}
	
	@Override
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
