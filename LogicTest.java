//@@author A0145732H
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {
	private Logic logic = new Logic();
	@Test
	public void testSearchTasks() throws Exception {
		StorageManagerStub sm = new StorageManagerStub();
		Logic commandLogic = new Logic();
		logic.init(sm, commandLogic);
				
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
		actual = logic.searchTasks(new String[] {"b"});
		expected.add(banana);
		expected.add(baby);
		expected.add(appleBanana);
		assert(actual.equals(expected));
		
		// test searching "apple b"
		// equivalence partition for searching with more than one keyword
		actual = logic.searchTasks(new String[] {"apple", "b"});
		expected.clear();
		expected.add(appleBanana);
		assert(actual.equals(expected));
		
		// test searching "c"
		// equivalence partition for searching and not finding anything
		actual = logic.searchTasks(new String[] {"c"});
		expected.clear();
		assert(actual.equals(expected));
	}
	
	@Test
	public void testGetTasksMethods() throws Exception {
		StorageManagerStub sm = new StorageManagerStub();
		Logic commandLogic = new Logic();
		logic.init(sm, commandLogic);
		
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
		
		ArrayList<Task> actual = logic.getCompletedTasks(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
		
		// test getUncompletedTasks()
		expected.add(t2);
		expected.add(t4);
		expected.add(t5);
		expected.add(t7);
		expected.add(t8);
		
		actual = logic.getUncompletedTasks();
		assert(actual.equals(expected));
		expected.clear();
		
		// test getUnscheduledTasks()
		expected.add(t1);
		expected.add(t2);
		
		actual = logic.getUnscheduledTasks(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
		
		// test getDeadlineTasks()
		expected.add(t3);
		expected.add(t4);
		expected.add(t5);
		
		actual = logic.getDeadlines(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
		
		// test getEvents()
		expected.add(t6);
		expected.add(t7);
		expected.add(t8);
		
		actual = logic.getEvents(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
		
		// test getTodaysTasks()
		expected.add(t3);
		expected.add(t4);
		
		actual = logic.getTodaysTasks(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
		
		// test getTomorrowsTasks()
		expected.add(t4);
		expected.add(t6);
		expected.add(t7);
		
		actual = logic.getTomorrowsTasks(sm.readAllTasks());
		assert(actual.equals(expected));
		expected.clear();
	}
	
	@Test
	public void testValidateDates() throws Exception {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime later = now.plusSeconds(1);
		
		// now is before later - should be valid
		
			logic.validateDates(now, later);

		// both dates are equal - should throw exception
		Exception exception = null;
		try {
			logic.validateDates(now, now);
		} catch (Exception e) {
			exception = e;
		}
		assert(exception != null);
		
		// later is before now - should throw exception
		exception = null;
		try {
			logic.validateDates(later, now);
		} catch (Exception e) {
			exception = e;
		}
		assert(exception != null);
	}
	
	@Test
	public void testUpdateCurrentTaskList() throws Exception {
		StorageManagerStub sm = new StorageManagerStub();
		Logic commandLogic = new Logic();
		logic.init(sm, commandLogic);
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
		actual = logic.updateCurrentTaskList();
		// these get methods are tested in the test suite - they are assumed to work here
		assert(logic.getUnscheduledTasks(actual).size() == Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(logic.getDeadlines(actual).size() == Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(logic.getEvents(actual).size() == Logic.DEFAULT_VIEW_NUM_EVENTS);
		sm.clearTasks();
		
		/* test that the default number of tasks is displayed when 
			there is only one type of task */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(events.get(i));
		}
		actual = logic.updateCurrentTaskList();
		assert(actual.size() == Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
		/* test that the correct number of tasks is displayed when
		 * there are only two types of tasks 
		 */
		for (int i = 0; i < Logic.DEFAULT_VIEW_MAX_TASKS; i++) {
			sm.writeTask(unscheduled.get(i));
			sm.writeTask(deadlines.get(i));
		}
		actual = logic.updateCurrentTaskList();
		assert(logic.getUnscheduledTasks(sm.readAllTasks()).size() > Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(logic.getDeadlines(sm.readAllTasks()).size() > Logic.DEFAULT_VIEW_NUM_DEADLINES);
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
		actual = logic.updateCurrentTaskList();
		assert(logic.getUnscheduledTasks(sm.readAllTasks()).size() > Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(logic.getDeadlines(sm.readAllTasks()).size() > Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(logic.getEvents(sm.readAllTasks()).size() < Logic.DEFAULT_VIEW_NUM_EVENTS);
		assert(actual.size() == Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
		/* test that the correct number of tasks is displayed when
		 * there are not enough tasks
		 */
		sm.writeTask(unscheduled.get(0));
		sm.writeTask(deadlines.get(0));
		sm.writeTask(events.get(0));
		actual = logic.updateCurrentTaskList();
		assert(logic.getUnscheduledTasks(sm.readAllTasks()).size() < Logic.DEFAULT_VIEW_NUM_UNSCHEDULED);
		assert(logic.getDeadlines(sm.readAllTasks()).size() < Logic.DEFAULT_VIEW_NUM_DEADLINES);
		assert(logic.getEvents(sm.readAllTasks()).size() < Logic.DEFAULT_VIEW_NUM_EVENTS);
		assert(actual.size() < Logic.DEFAULT_VIEW_MAX_TASKS);
		sm.clearTasks();
		
	}
}
