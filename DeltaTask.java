//@@author A0126270N
import java.time.LocalDateTime;

/*
 *This class stores info about what changes users want to make to an existing task
 *Used by CommandParser to communicate with the Update command class.  
 *@author Dickson
 */

public class DeltaTask {
	private String name;
	private LocalDateTime start, end;
	
	// null cannot be used to indicate the absence of changes, because we must differentiate between 
	// the user making no change, or wanting to delete information in that field
	public enum FIELD_ACTION {
		NONE, UPDATE, REMOVE
	}
	
	private FIELD_ACTION nameAction, startAction, endAction;
	
	public DeltaTask(FIELD_ACTION nAction, String newName, FIELD_ACTION sAction, LocalDateTime newStart, FIELD_ACTION eAction, LocalDateTime newEnd) {
		nameAction = nAction;
		startAction = sAction;
		endAction = eAction;
		name = newName;
		start = newStart;
		end = newEnd;
	}
	
	public FIELD_ACTION getNameAction() {
		return nameAction;
	}
	
	public FIELD_ACTION getStartAction() {
		return startAction;
	}
	
	public FIELD_ACTION getEndAction() {
		return endAction;
	}
	
	public String getNewName() {
		return name;
	}
	
	public LocalDateTime getNewStart() {
		return start;
	}
	
	public LocalDateTime getNewEnd() {
		return end;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		DeltaTask other = (DeltaTask)obj;
boolean isNameEqual = (name == null && other.getNewName() == null) || (name != null && name.equals(other.getNewName()));
boolean isStartEqual = (start == null && other.getNewStart() == null) || (start != null && start.equals(other.getNewStart()));
boolean isEndEqual = (end == null && other.getNewEnd() == null) || (end != null && end.equals(other.getNewEnd()));

return nameAction == other.getNameAction() && endAction == other.getEndAction() && startAction == other.getStartAction() && isNameEqual && isEndEqual && isStartEqual;
	}
}
