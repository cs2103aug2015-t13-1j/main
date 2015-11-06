
public class Reformat extends Command {
	private static final String SUCCESS_REFORMAT = "Storage was reformatted";
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		storageManager.clearTask();
	}

	@Override
	public String getSuccessMessage() {
		// TODO Auto-generated method stub
		return SUCCESS_REFORMAT;
	}
}
