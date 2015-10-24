import java.time.LocalDateTime;

/**
 * Task is a class that contains all the required information for Command to
 * understand the task.
 */
public class Task {
	private String name;
	private LocalDateTime start = null;
	private LocalDateTime end = null;
boolean isDone; // used to mark tasks as complete

	public Task(String name, boolean isDone) {
		this(name, null, null, isDone);
	}
	
	public Task(String name, LocalDateTime end, boolean isDone) {
		this(name, null, end, isDone);
	}
	
	public Task(String name, LocalDateTime start, LocalDateTime end, boolean isDone) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.isDone = isDone;
	}
	    
	public String getName() {
		return name;
	}
	  
	public LocalDateTime getStartDateTime() {
		return this.start;
	}
	  
	public LocalDateTime getEndDateTime() {
		return this.end;
	} 
	
	public boolean isDone() {
		return isDone;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) { 
			return false; 
		}
		
		Task other = (Task)obj;
		LocalDateTime otherStart = other.getStartDateTime();
		LocalDateTime otherEnd = other.getEndDateTime();
		boolean isNameEqual = false, isStartEqual = false, isEndEqual = false;
		
		if (this.getName().equals(other.getName())) {
			isNameEqual = true;
		}
		
	if ((end == null && end == otherEnd) || (end != null && end.equals(otherEnd))) {
		isEndEqual = true;
	}
	
	if ((start == null && start == otherStart) || (start != null && start.equals(otherStart))) {
		isStartEqual = true;
	}
	
	return isNameEqual && isEndEqual && isStartEqual;
	}
}