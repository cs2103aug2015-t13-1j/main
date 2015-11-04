
public class Relocate extends Command {
	private static final String SUCCESS_RELOCATE = "Storage was relocated to \"%s\"";
	private static String fileLocation = "";
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		fileLocation = "./Dropbox/";
		if (!StorageManager.changeStorageLocation("./Dropbox/")) {
			throw new Exception("File directory specified does not exist");
		}
	}

	@Override
	public String getSuccessMessage() {
		// TODO Auto-generated method stub
		return String.format(SUCCESS_RELOCATE, fileLocation);
	}
}
