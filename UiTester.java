//@@A0145732H
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class UiTester {

	@Test
	public void testWriteEventToList() {
		StringBuilder actual = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		int taskNumber = 0;
		boolean isFirst = true;
		Task task;
		String name = "test";
		LocalDateTime start;
		LocalDateTime end;
		
		// test writing the first event for an uncompleted event starting and ending on the same day
		start = LocalDateTime.now().plusDays(1);
		end = LocalDateTime.now().plusDays(1);
		task = new Task(name, start, end, false);
		Ui.writeEventToList(actual, taskNumber, task, isFirst);
		expected.append(Ui.MESSAGE_EVENTS_HEADER);
		expected.append(String.format(Ui.MESSAGE_EVENT, Ui.MARKER_UNCOMPLETED, taskNumber, name, 
				Ui.getDateTimeFormat(start), Ui.getTimeFormat(end)));
		assertEquals(expected.toString(), actual.toString());
		
		// test writing a completed event starting and ending on different days, and is not the first event
		actual = new StringBuilder();
		expected = new StringBuilder();
		start = LocalDateTime.now().plusDays(1);
		end = start.plusDays(2);
		task = new Task(name, start, end, true);
		isFirst = false;
		Ui.writeEventToList(actual, taskNumber, task, isFirst);
		expected.append(String.format(Ui.MESSAGE_EVENT, Ui.MARKER_DONE, taskNumber, name, 
				Ui.getDateTimeFormat(start), Ui.getDateTimeFormat(end)));
		assertEquals(expected.toString(), actual.toString());
	}
	
	@Test
	public void testWriteDeadlineToList() {
		StringBuilder actual = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		int taskNumber = 0;
		boolean isFirst = true;
		Task task;
		String name = "test";
		LocalDateTime end;
		
		// test writing the first deadline for an uncompleted deadline
		end = LocalDateTime.now();
		task = new Task(name, end, false);
		Ui.writeDeadlineToList(actual, taskNumber, task, isFirst);
		expected.append(Ui.MESSAGE_DEADLINE_HEADER);
		expected.append(String.format(Ui.MESSAGE_DEADLINE, Ui.MARKER_UNCOMPLETED, taskNumber, name, 
				Ui.getDateFormat(end), Ui.getTimeFormat(end)));
		assertEquals(expected.toString(), actual.toString());
		
		// test writing a completed deadline that is not the first deadline
		task = new Task(name, end, true);
		isFirst = false;
		Ui.writeDeadlineToList(actual, taskNumber, task, isFirst);
		expected.append(String.format(Ui.MESSAGE_DEADLINE, Ui.MARKER_DONE, taskNumber, name, 
				Ui.getDateFormat(end), Ui.getTimeFormat(end)));
		assert(actual.equals(expected));
	}
	
	@Test
	public void testWriteUnscheduledToList() {
		StringBuilder actual = new StringBuilder();
		StringBuilder expected = new StringBuilder();
		int taskNumber = 0;
		boolean isFirst = true;
		Task task;
		String name = "test";
		
		// test writing the first unscheduled for an uncompleted unscheduled task
		task = new Task(name, false);
		Ui.writeUnscheduledToList(actual, taskNumber, task, isFirst);
		expected.append(Ui.MESSAGE_UNSCHEDULED_HEADER);
		expected.append(String.format(Ui.MESSAGE_UNSCHEDULED, Ui.MARKER_UNCOMPLETED, taskNumber, name));
		assertEquals(expected.toString(), actual.toString());
		
		// test writing a completed unscheduled task that is not the first one
		task = new Task(name, true);
		isFirst = false;
		Ui.writeUnscheduledToList(actual, taskNumber, task, isFirst);
		expected.append(String.format(Ui.MESSAGE_UNSCHEDULED, Ui.MARKER_DONE, taskNumber, name));
		assertEquals(expected.toString(), actual.toString());
	}

	@Test 
	public void testAddColorCoding() {
		String message = "test";
		String actual;
		
		// test that overdue color is added
		actual = Ui.addColorCoding(message, LocalDateTime.now().minusDays(1));
		assert(actual.startsWith(Ui.COLOR_CODE_OVERDUE));
		
		// test that today color is added
		actual = Ui.addColorCoding(message, LocalDateTime.now());
		assert(actual.startsWith(Ui.COLOR_CODE_TODAY));
		
		// test that tomorrow color is added
		actual = Ui.addColorCoding(message, LocalDateTime.now().plusDays(1));
		assert(actual.startsWith(Ui.COLOR_CODE_TOMORROW));
		
		// test that future color is added
		actual = Ui.addColorCoding(message, LocalDateTime.now().plusDays(3));
		assert(actual.startsWith(Ui.COLOR_CODE_FUTURE));
	}
}
