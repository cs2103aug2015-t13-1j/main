//@@author A0100081E
public class Reformat extends Command {
	private static final String SUCCESS_REFORMAT = "Storage was reformatted";
	
	@Override
	public void execute() throws Exception {
		storageManager.clearTask();
	}

	@Override
	public String getSuccessMessage() {
		return SUCCESS_REFORMAT;
	}
}
