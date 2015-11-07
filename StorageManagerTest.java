import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageManagerTest {
	// Note: running the tests causes it to read and write from the eclipse project root folder instead of /bin. 
	// Not sure if this can be changed
	private static final String DIRECTORY = "unit tests\\";
	private static final String FILENAME = "TaskStorage.json";
	private static StorageManager storageManager = null;

	@Before
	public void init() throws Exception {
		storageManager = new StorageManager();
		// storageManager.initializeStorage();
		storageManager.openStorage();
		storageManager.changeStorageLocation(DIRECTORY);
	}
		
	@After
	public void shutdown() throws Exception {
		storageManager.closeStorage();
		deleteFile(DIRECTORY + FILENAME);
	}
	
	private static void deleteFile(String filename) throws IOException {
		File file = new File(filename);
		if (file.exists() == false) {
			throw new IOException("File does not exist");
		}
		
		if (file.delete() == false) {
			throw new IOException("Could not delete file");
		}
	}

	@Test
	public void test() {
		
	
	}
}
	
	/* 
	@Test
	public void testUpdateTask() {
		
		// StorageManager.openStorage();
		
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
	
	//@@author A0145732H
	@Test
	// testing whether the tasks sort properly
	public void testReadAllTasks() throws Exception {
		Task e1 = new Task("event 1", LocalDateTime.of(2015, 1, 1, 1, 0), LocalDateTime.of(2015, 1, 2, 1, 0), false);
		Task e2 = new Task("event 2", LocalDateTime.of(2015, 1, 1, 1, 0), LocalDateTime.of(2015, 1, 3, 1, 0), false);
		Task e3 = new Task("event 3", LocalDateTime.of(2015, 1, 2, 1, 0), LocalDateTime.of(2015, 1, 3, 1, 0), false);
		Task e4 = new Task("event 4", LocalDateTime.of(2015, 1, 2, 1, 0), LocalDateTime.of(2015, 1, 3, 1, 0), false);
		Task d1 = new Task("deadline 1", LocalDateTime.of(2015, 1, 1, 1, 0), false);
		Task d2 = new Task("deadline 2", LocalDateTime.of(2015, 1, 2, 1, 0), false);
		Task d3 = new Task("deadline 3", LocalDateTime.of(2015, 1, 2, 1, 0), false);
		Task f1 = new Task("floating 1", false);
		Task f2 = new Task("floating 2", false);
		
		StorageManager.writeTask(e1);
		StorageManager.writeTask(d1);
		StorageManager.writeTask(f1);
		StorageManager.writeTask(e2);
		StorageManager.writeTask(d2);
		StorageManager.writeTask(f2);
		StorageManager.writeTask(e3);
		StorageManager.writeTask(d3);
		StorageManager.writeTask(e4);
		
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(e1);
		expected.add(e2);
		expected.add(e3);
		expected.add(e4);
		expected.add(d1);
		expected.add(d2);
		expected.add(d3);
		expected.add(f1);
		expected.add(f2);
		
		ArrayList<Task> actual = StorageManager.readAllTasks();
		assertEquals(expected, actual);
		
		for (Task task : expected) {
			StorageManager.removeTask(task);
		}
	}
*/
