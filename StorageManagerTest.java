import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class StorageManagerTest {

	@Test
	public void testUpdateTask() {
		StorageManager.openStorage();
		
		Task oldTask = new Task("old task", false);
		Task newTask = new Task("new task", false);
		Task notFound = new Task("not found", false);
		
		// test thrown exception for no tasks in list
		try {
			StorageManager.updateTask(oldTask, newTask);
		} catch (Exception e) {
			Exception expected = new Exception("You currently do not have any tasks saved.");
			assertEquals(expected.getMessage(), e.getMessage());
		}
		
		// test thrown exception for task not found
		StorageManager.writeTask(notFound);
		try {
			StorageManager.updateTask(oldTask, newTask);
		} catch (Exception e) {
			Exception expected = new Exception("Task \"" + oldTask.getName() + "\" was not found.");
			assertEquals(expected.getMessage(), e.getMessage());
		}
		
		// test updating "old task" to "new task"
		StorageManager.writeTask(oldTask);
		try {
			StorageManager.updateTask(oldTask, newTask);
		} catch (Exception e) {			
		}
		ArrayList<Task> actual = StorageManager.readAllTasks();
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(notFound);
		expected.add(newTask);
		assertEquals(expected, actual);
		
		// reset storage file
		for (Task task : actual) {
			try {
				StorageManager.removeTask(task);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StorageManager.closeStorage();
	}

}
