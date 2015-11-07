
public class Relocate extends Command {
	private final String SUCCESS_RELOCATE = "Storage was relocated to \"%s\"";
	private String fileLocation = null;
	
	public Relocate(String fileLocation) {
		assert(fileLocation != null);
		this.fileLocation = fileLocation;
	}
	
	@Override
	public void execute() throws Exception {
		if (fileLocation.charAt(fileLocation.length() - 1) != '/') {
			throw new Exception("File directory must contain \"/\" at the end");
		}
		
		if (!storageManager.changeStorageLocation(fileLocation)) {
			throw new Exception("File directory specified does not exist");
		}
	}

	@Override
	public String getSuccessMessage() {
		return String.format(SUCCESS_RELOCATE, fileLocation);
	}
	
	//@@author A0126270N
	public String getFileLocation() {
		return fileLocation;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Relocate other = (Relocate)obj;
		return fileLocation.equals(other.getFileLocation()); 		
	}
}
