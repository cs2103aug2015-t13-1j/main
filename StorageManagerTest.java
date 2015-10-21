import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class StorageManagerTest {

	@Test
	public void testSearchTasks() {
		StorageManager.openStorage();
		
		Task apple = new Task("apple");
		Task banana = new Task("banana");
		Task baby = new Task("baby");
		Task appleBanana = new Task("apple banana");
		
		StorageManager.writeTask(apple);
		StorageManager.writeTask(banana);
		StorageManager.writeTask(baby);
		StorageManager.writeTask(appleBanana);
		
		ArrayList<Task> expected = new ArrayList<Task>();
		ArrayList<Task> actual;
		
		// test searching "b"
		// equivalence partition for searching for one keyword
		actual = StorageManager.searchTasks(new String[] {"b"});
		expected.add(banana);
		expected.add(baby);
		expected.add(appleBanana);
		assertEquals(expected, actual);
		
		// test searching "apple b"
		// equivalence partition for searching with more than one keyword
		actual = StorageManager.searchTasks(new String[] {"apple", "b"});
		expected.clear();
		expected.add(appleBanana);
		assertEquals(expected, actual);
		
		// test searching "c"
		// equivalence partition for searching and not finding anything
		actual = StorageManager.searchTasks(new String[] {"c"});
		expected.clear();
		assertEquals(expected, actual);
		
		// reset the storage file
		try {
			StorageManager.removeTask(apple);
			StorageManager.removeTask(baby);
			StorageManager.removeTask(banana);
			StorageManager.removeTask(appleBanana);
			StorageManager.closeStorage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateTask() {
		StorageManager.openStorage();
		
		Task oldTask = new Task("old task");
		Task newTask = new Task("new task");
		Task notFound = new Task("not found");
		
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
		Task[] actual = StorageManager.readAllTasks();
		Task[] expected = new Task[] {notFound, newTask};
		assertArrayEquals(expected, actual);
		
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
