import java.time.LOCALDATETIME;

/**
 * Task is a class that contains all the required information for Command to
 * understand the task.
 */
public class Task {
	private String name;
    private LOCALDATETIME start = null;
    private LOCALDATETIME end = null;

    public Task() {
    }

    public Task(String name) {
    	this.name = name;
    }

    public Task(String name, LOCALDATETIME end) {
    	this.name = name;
    	this.end = end;
    }

    public Task(String name, LOCALDATETIME start, LOCALDATETIME end) {
    	this.name = name;
    	this.start = start;
    	this.end = end;
    }
}