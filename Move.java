import java.io.File;
import java.util.logging.Level;

//@@author A0100081E
public class Move extends Command implements Undoable {
	private static final String SUCCESS_MOVE = "Storage was moved to \"%s\"";
	private static final String SUCCESS_UNDO = "Storage was moved back to the original location \"%s\"";
	private static final String ERROR_INVALID_FOLDER_PATH = "%s is not a valid path to an existing folder.";
	private String newLocation = null;
	private String oldLocation = null;
	
	public Move(String fileLocation) {
		assert(fileLocation != null);
		newLocation = fileLocation;
	}
	
	@Override
	public void execute() throws Exception {
		oldLocation = storageManager.getStorageDirectory();
		File newPath = new File(newLocation);
		if (newPath.isDirectory() == false) {
	  		log.log(Level.INFO, "aborting because the folder path is invalid\n");
	  		throw new Exception(String.format(ERROR_INVALID_FOLDER_PATH, newLocation));
	  	}
		storageManager.changeStorageLocation(newLocation);
		}

	@Override
	public String getSuccessMessage() {
		return String.format(SUCCESS_MOVE, newLocation);
	}
	
	//@@author A0126270N
	public String getNewLocation() {
		return newLocation;
	}
	
	public String getOldLocation() {
		return oldLocation;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Move other = (Move)obj;
		boolean isOldLocationEqual = (oldLocation == null && other.getOldLocation() == null) || (oldLocation != null && oldLocation.equals(other.getOldLocation()));
		return isOldLocationEqual && newLocation.equals(other.getNewLocation()); 		
	}
	
	public void undo() throws Exception {
		File oldPath = new File(oldLocation);
		if (oldPath.isDirectory() == false) {
	  		log.log(Level.INFO, "aborting because the old folder path is invalid\n");
	  		throw new Exception("Undo failed. " + String.format(ERROR_INVALID_FOLDER_PATH, oldLocation));
	  	}
		storageManager.changeStorageLocation(oldLocation);
	}
	
	public String getUndoMessage() {
		return String.format(SUCCESS_UNDO, oldLocation);
}
}
