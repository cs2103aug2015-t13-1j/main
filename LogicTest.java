import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {

	@Test
	public void testSearchTasks() {
		StorageManager.openStorage();
		
		Task apple = new Task("apple", false);
		Task banana = new Task("banana", false);
		Task baby = new Task("baby", false);
		Task appleBanana = new Task("apple banana", false);
		
		StorageManager.writeTask(apple);
		StorageManager.writeTask(banana);
		StorageManager.writeTask(baby);
		StorageManager.writeTask(appleBanana);
		
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
	public void testGetSortedTasks() throws Exception {
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
		
		ArrayList<Task> actual = Logic.getSortedTasks();
		assertEquals(expected, actual);
		
		for (Task task : expected) {
			StorageManager.removeTask(task);
		}
	}
}
