import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {
	//@@author A0145732H
	@Test
	public void testSearchTasks() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.init(sm);
				
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
	public void testGetTasksMethods() {
		StorageManagerStub sm = new StorageManagerStub();
		Logic.init(sm);
		
		Task t1 = new Task("1", true);
		Task t2 = new Task("2", false);
		Task t3 = new Task("3", LocalDateTime.now(), true);
		Task t4 = new Task("4", LocalDateTime.now().plusDays(1), false);
		Task t5 = new Task("5", LocalDateTime.now().plusDays(2), false);
		Task t6 = new Task("6", LocalDateTime.now(), LocalDateTime.now().plusDays(1), true);
		Task t7 = new Task("7", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), false);
		Task t8 = new Task("8", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), false);
		
		sm.writeTask(t1);
		sm.writeTask(t2);
		sm.writeTask(t3);
		sm.writeTask(t4);
		sm.writeTask(t5);
		sm.writeTask(t6);
		sm.writeTask(t7);
		sm.writeTask(t8);
		
		// test getCompletedTasks()
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(t1);
		expected.add(t3);
		expected.add(t6);
		
		ArrayList<Task> actual = Logic.getCompletedTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getUncompletedTasks()
		expected.add(t2);
		expected.add(t4);
		expected.add(t5);
		expected.add(t7);
		expected.add(t8);
		
		actual = Logic.getUncompletedTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getUnscheduledTasks()
		expected.add(t1);
		expected.add(t2);
		
		actual = Logic.getUnscheduledTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getDeadlineTasks()
		expected.add(t3);
		expected.add(t4);
		expected.add(t5);
		
		actual = Logic.getDeadlineTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getEvents()
		expected.add(t6);
		expected.add(t7);
		expected.add(t8);
		
		actual = Logic.getEvents();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getTodaysTasks()
		expected.add(t3);
		expected.add(t4);
		
		actual = Logic.getTodaysTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getTomorrowsTasks()
		expected.add(t4);
		expected.add(t6);
		expected.add(t7);
		
		actual = Logic.getTomorrowsTasks();
		assert(actual.equals(expected));
		expected.clear();
	}
	
	@Test
	public void testCompareDates() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrow = now.plusDays(1);
		
		int comparison = Logic.compareDates(now, tomorrow);
		assert(comparison < 0);
		comparison = Logic.compareDates(tomorrow, now);
		assert(comparison > 0);
		comparison = Logic.compareDates(now, now);
		assert(comparison == 0);
	}
	
	@Test
	public void testValidateDates() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime later = now.plusSeconds(1);
		
		// now is before later - should be valid
		try {
			Logic.validateDates(now, later);
		} catch (Exception e) {
			fail();
		}

		// both dates are equal - should throw exception
		Exception exception = null;
		try {
			Logic.validateDates(now, now);
		} catch (Exception e) {
			exception = e;
		}
		assert(exception != null);
		
		// later is before now - should throw exception
		exception = null;
		try {
			Logic.validateDates(later, now);
		} catch (Exception e) {
			exception = e;
		}
		assert(exception != null);
	}
	
	@Test
	public void testUpdateCurrentTaskList() {
		StorageManagerStub sm = new StorageManagerStub();
		ArrayList<Task> unscheduled = new ArrayList<Task>();
		ArrayList<Task> deadlines = new ArrayList<Task>();
		ArrayList<Task> events = new ArrayList<Task>();
		ArrayList<Task> actual;
		
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			unscheduled.add(new Task("unscheduled " + i, false));
			deadlines.add(new Task("deadline " + i, LocalDateTime.now(), false));
			events.add(new Task("event " + i, LocalDateTime.now(), LocalDateTime.now().plusDays(1), false));
		}
		
		/* test that default display shows the right number of each 
			task type when there are enough of each type */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(unscheduled.get(i));
			sm.writeTask(deadlines.get(i));
			sm.writeTask(events.get(i));
		}
		actual = Logic.updateCurrentTaskList();
		// these get methods are tested in the test suite - they are assumed to work here
		assert(Logic.getUnscheduledTasks(actual).size() == Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(Logic.getDeadlineTasks(actual).size() == Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(Logic.getEvents(actual).size() == Logic.DEFAULT_VIEW_NUM_EVENTS);
		sm.clearTasks();
		
		/* test that the default number of tasks is displayed when 
			there is only one type of task */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(events.get(i));
		}
		actual = Logic.updateCurrentTaskList();
		assert(actual.size() == Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
		/* test that the correct number of tasks is displayed when
		 * there are only two types of tasks 
		 */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(unscheduled.get(i));
			sm.writeTask(deadlines.get(i));
		}
		actual = Logic.updateCurrentTaskList();
		assert(Logic.getUnscheduledTasks().size() > Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(Logic.getDeadlineTasks().size() > Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(actual.size() == Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
		/* test that the correct number of tasks is displayed when
		 * there are not enough of one type of task, but enough of 
		 * the other two to compensate. 
		 */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(unscheduled.get(i));
			sm.writeTask(deadlines.get(i));
		}
		sm.writeTask(events.get(0));
		actual = Logic.updateCurrentTaskList();
		assert(Logic.getUnscheduledTasks().size() > Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(Logic.getDeadlineTasks().size() > Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(Logic.getEvents().size() < Logic.DEFAULT_VIEW_NUM_EVENTS);
		assert(actual.size() == Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
		/* test that the correct number of tasks is displayed when
		 * there are not enough tasks
		 */
		sm.writeTask(unscheduled.get(0));
		sm.writeTask(deadlines.get(0));
		sm.writeTask(events.get(0));
		actual = Logic.updateCurrentTaskList();
		assert(Logic.getUnscheduledTasks().size() < Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(Logic.getDeadlineTasks().size() < Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(Logic.getEvents().size() < Logic.DEFAULT_VIEW_NUM_EVENTS);
		assert(actual.size() < Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
	}
}
