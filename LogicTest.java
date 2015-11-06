import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {
	//@@author A0145732H
	@Test
	public void testSearchTasks() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.setStorageManager(sm);
				
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
		assert(actual.equals(expected));
		
		// test searching "apple b"
		// equivalence partition for searching with more than one keyword
		actual = Logic.searchTasks(new String[] {"apple", "b"});
		expected.clear();
		expected.add(appleBanana);
		assert(actual.equals(expected));
		
		// test searching "c"
		// equivalence partition for searching and not finding anything
		actual = Logic.searchTasks(new String[] {"c"});
		expected.clear();
		assert(actual.equals(expected));
	}
	
	@Test
	public void testGetCompletedTasks() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.setStorageManager(sm);
		
		Task t1 = new Task("1", true);
		Task t2 = new Task("2", false);
		Task t3 = new Task("3", LocalDateTime.now(), true);
		Task t4 = new Task("4", LocalDateTime.now(), false);
		Task t5 = new Task("5", LocalDateTime.now(), LocalDateTime.now(), true);
		Task t6 = new Task("6", LocalDateTime.now(), LocalDateTime.now(), false);
		
		sm.writeTask(t1);
		sm.writeTask(t2);
		sm.writeTask(t3);
		sm.writeTask(t4);
		sm.writeTask(t5);
		sm.writeTask(t6);
		
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(t1);
		expected.add(t3);
		expected.add(t5);
		
		ArrayList<Task> actual = Logic.getCompletedTasks();
		assert(actual.equals(expected));
	}
}
