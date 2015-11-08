//@@author A0100081E
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageManagerTest {
	// Note: running the tests causes it to read and write from the eclipse project root folder instead of /bin. 
	// Not sure if this can be changed
	private static final String DIRECTORY = "./";
	private static final String FILENAME = "TaskStorage.json";
	private static StorageManager storageManager = new StorageManager();

	@Before
	public void testOpenStorage() throws Exception {
		// test openStorage() first so that the storage is open for the other tests
		// as long as it passes
		storageManager.openStorage();
		
		assertNotEquals(storageManager.getStorageDirectory(), "");
		assertNotEquals(storageManager.getStorageName(), "");
		assertNotEquals(storageManager.getStorageType(), "");
		assertEquals(storageManager.getStorageFile().exists(), true);
	}
		
	@After
	public void shutdown() throws Exception {
		storageManager.closeStorage();
		deleteFile(DIRECTORY + FILENAME);

		try {
			storageManager.getStorageFile().exists();
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), null);
    }
	}
	
	private static void deleteFile(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			throw new IOException("File does not exist");
		}
		
		if (!file.delete()) {
			throw new IOException("Could not delete file");
		}
	}
	
	@Test
	public void testChangeStorageLocation() throws Exception {
		storageManager.changeStorageLocation("./");
		
		assertEquals(storageManager.getStorageDirectory(), "./");
	}
	
	@Test
	public void testTaskFunctions() throws Exception {
		Task task1 = new Task("1", false);
		Task task2 = new Task("2", false);
		Task task3 = new Task("3", false);
		Task task4 = new Task("4", false);
		Task task5 = new Task("5", false);
		ArrayList<Task> taskList = new ArrayList<Task>();
		ArrayList<Task> emptyList = new ArrayList<Task>();
		emptyList.clear();
		
		storageManager.writeTask(task1);
		storageManager.writeTask(task2);
		storageManager.writeTask(task3);
		storageManager.writeTask(task4);
		storageManager.writeTask(task5);
		taskList.add(task1);
		taskList.add(task2);
		taskList.add(task3);
		taskList.add(task4);
		taskList.add(task5);
		
		assertEquals(storageManager.readAllTasks(), taskList); // Tests writeTask() and readAllTasks()
		
		storageManager.removeTask(task3);
		taskList.remove(2); // index 2 contains task3
		
		assertEquals(storageManager.readAllTasks(), taskList); // Tests removeTask()
		
		Task task6 = new Task("6", false);
		
		storageManager.updateTask(task5, task6);
		
		taskList.remove(3);
		taskList.add(task6);
		
		assertEquals(storageManager.readAllTasks().get(3).getName(), taskList.get(3).getName()); // Tests updateTask()
		
		storageManager.clearTask();
		
		assertEquals(storageManager.readAllTasks(), emptyList); // Tests clearTask()
	}
	
	@Test
	public void testAccessFunctions() throws Exception {
		assertEquals(storageManager.getStorageDirectory(), "./"); // Tests getStorageDirectory()
		assertEquals(storageManager.getStorageName(), "TaskStorage"); // Tests getStorageName()
		assertEquals(storageManager.getStorageType(), ".json"); // Tests getStorageType()
		assertEquals(storageManager.getInformationDirectory(), "./"); // Tests getInformationDirectory()
		assertEquals(storageManager.getStorageFile(), new File(storageManager.getStorageDirectory() + storageManager.getStorageName() + storageManager.getStorageType())); // Tests getStorageFile()
	}
	
}
