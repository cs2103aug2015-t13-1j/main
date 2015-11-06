import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {
	//@@author A0145732H
	@Test
	public void testSearchTasks() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.setStorageManager(sm);
		
		sm.openStorage();
		
		Task apple = new Task("apple", false);
		Task banana = new Task("banana", false);
		Task baby = new Task("baby", false);
		Task appleBanana = new Task("apple banana", false);
		
		sm.writeTask(apple);
		sm.writeTask(banana);
		sm.writeTask(baby);
		sm.writeTask(appleBanana);
		
		ArrayList<Task> expected = new ArrayList<Task>();
		ArrayList<Task> actual;
		
		// test searching "b"
		// equivalence partition for searching for one keyword
		actual = Logic.searchTasks(new String[] {"b"});
		expected.add(banana);
		expected.add(baby);
		expected.add(appleBanana);
		assertEquals(expected, actual);
		
		// test searching "apple b"
		// equivalence partition for searching with more than one keyword
		actual = Logic.searchTasks(new String[] {"apple", "b"});
		expected.clear();
		expected.add(appleBanana);
		assertEquals(expected, actual);
		
		// test searching "c"
		// equivalence partition for searching and not finding anything
		actual = Logic.searchTasks(new String[] {"c"});
		expected.clear();
		assertEquals(expected, actual);
		
		// reset the storage file
		try {
			sm.removeTask(apple);
			sm.removeTask(baby);
			sm.removeTask(banana);
			sm.removeTask(appleBanana);
			sm.closeStorage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetCompletedTasks() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.setStorageManager(sm);
		
		ArrayList<Task> completed = Logic.getCompletedTasks();
		assertEquals(new ArrayList<Task>(), completed);
	}
}
