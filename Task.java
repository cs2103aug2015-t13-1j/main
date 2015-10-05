//import java.time.LocalDateTime;

/**
 * Task is a class that contains all the required information for Command to
 * understand the task.
 */
public class Task {
		private String name;
    private LocalDateTime start = null;
    private LocalDateTime end = null;

    public Task(String name) {
    	this.name = name;
    }

    public Task(String name, LocalDateTime end) {
    	this.name = name;
    	this.end = end;
    }

    public Task(String name, LocalDateTime start, LocalDateTime end) {
    	this.name = name;
    	this.start = start;
    	this.end = end;
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
    
}