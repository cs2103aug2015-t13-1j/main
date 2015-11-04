//@@author A0126270N
import java.time.LocalDateTime;

/**
 * Task is a class that contains all the required information for Command to
 * understand the task.
 */
public class Task implements Comparable<Task> {
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

	public void setDone(boolean isDone) {
		this.isDone = isDone;
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
		boolean isNameEqual = this.getName().equals(other.getName());
				boolean isStartEqual = (start == null && start == otherStart) || (start != null && start.equals(otherStart));
				boolean isEndEqual = (end == null && end == otherEnd) || (end != null && end.equals(otherEnd));
		
		return isNameEqual && isEndEqual && isStartEqual;
	}
	
	//@@author A0145732H
	@Override
	/**
	 * Tasks are ordered by events, deadlines, then floating tasks (ascending).
	 * Events are compared to other events first by start date, then by end date, then by name.
	 * Deadlines are compared to other deadlines first by end date, then by name.
	 * Floating tasks are compared to other floating tasks by name.
	 */
	public int compareTo(Task otherTask) {
		if (otherTask == null) {
			throw new NullPointerException();
		}
		if (this.equals(otherTask)) {
			return 0;
		}
		
		LocalDateTime otherStart = otherTask.getStartDateTime();
		LocalDateTime otherEnd = otherTask.getEndDateTime();
		String otherName = otherTask.getName();
		
		// If this is an event:
		if (this.start != null && this.end != null) {
			// if other is not an event: this < other
			if (otherStart == null) {
				return -1;
			} else {
				// sort order: start date, end date, name
				if (!this.start.equals(otherStart)) {
					return (this.start.compareTo(otherStart));
				} else if (!this.end.equals(otherEnd)) {
					return (this.end.compareTo(otherEnd));
				} else {
					return (this.name.compareTo(otherName));
				}
			}
		}
		
		// If this is a deadline task - sorted by end date, then name
		if (this.end != null && this.start == null) {
			if (otherStart != null) {
				// this is a deadline, other is an event --> this > other
				return 1;
			} else if (otherEnd == null) {
				// this is a deadline, other is floating --> this < other
				return -1;
			} else {
				// both this and other are deadlines: sort by end date then by name
				if (!this.end.equals(otherEnd)) {
					return (this.end.compareTo(otherEnd));
				} else {
					return (this.name.compareTo(otherName));
				}
			}
		}
		// 3. floating tasks - sorted by name
		if (this.end == null && this.start == null) {
			if (otherStart != null || otherEnd != null) {
				// this is a floating, other is not floating --> this > other
				return 1;
			} else {
				// both are floating --> sort by name
				return (this.name.compareTo(otherName));
			}
		}
		return 0;
		
	}
}