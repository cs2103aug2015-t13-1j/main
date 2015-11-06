import java.util.ArrayList;

public class StorageManagerStub extends StorageManager {
	ArrayList<Task> tasks = new ArrayList<Task>();
	
	public StorageManagerStub() {
	}
	
	public void writeTask(Task task) {
		tasks.add(task);
	}
	public ArrayList<Task> readAllTasks() {
		return tasks;
	}
}
