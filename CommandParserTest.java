import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

//@@author A0126270N
public class CommandParserTest {
	
	@Test
	public void testUnsupportedCommandParsing() {
		try {
			CommandParser.getCommandFromInput("abc");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_COMMAND, "abc"));
		}
	}

	@Test
	public void testListCommandParsing() throws Exception {
			Command valid = CommandParser.getCommandFromInput("list");
			assertEquals(List.class, valid.getClass());
	}
	
	@Test
	public void testExitCommandParsing() throws Exception {
			Command valid1 = CommandParser.getCommandFromInput("exit");
			assertEquals(Exit.class, valid1.getClass());
			Command valid2 = CommandParser.getCommandFromInput("Quit");
			assertEquals(Exit.class, valid2.getClass());
	}
	
	@Test
	public void testParsingOfEmptyString() {
		try {
			CommandParser.getCommandFromInput("");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NOTHING_ENTERED);	
		}
		
	}
	
	@Test
	public void testAddUnscheduledTaskCommandParsing() throws Exception {
		try {
			CommandParser.getCommandFromInput("add ");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_ADD);
		}
		
		String newTaskName = "read To Kill a Mockingbird";
		
		// task names should be quoted
		try {
			CommandParser.getCommandFromInput("add " + newTaskName);
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NAME_SHOULD_BE_IN_QUOTES);
		}
	
			Command valid1 = CommandParser.getCommandFromInput("add \"" + newTaskName + "\"");
			assertEquals(new Add(new Task(newTaskName, false)), valid1);
		
		try {
			CommandParser.getCommandFromInput("add \" \"");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NAME_SHOULD_CONTAIN_NON_WHITESPACE_CHARS);
		}
			
	}

	@Test
	public void testAddDeadlineTaskCommandParsing() throws Exception {
		String newTaskName = "read Harry Potter by J K Rowling";
		// boundary case heuristic: the word to being present in the title should not cause parsing problems
		String validDeadlineString = "21-12-2015 14:40";
		LocalDateTime validDeadline = CommandParser.parseDateTime(validDeadlineString);
		String invalidDeadlineString = "21-13-2015 14:40";
	
			Add valid1 = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + validDeadlineString);
			assertEquals(valid1.getTask().getEndDateTime(), validDeadline);
			assertEquals(new Add(new Task(newTaskName, validDeadline, false)), valid1);
		
		try {
			CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + invalidDeadlineString);
			fail("exception not thrown");
		} catch (Exception e) {
		assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidDeadlineString));
		}

		try {
			CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by ");
			fail("exception not thrown");
		} catch (Exception e) {
		assertEquals(e.getMessage(), CommandParser.ERROR_COULD_NOT_DETERMINE_TASK_TYPE_TO_ADD);
		}
	}
	
	@Test
	public void testAddEventTaskCommandParsing() throws Exception {
		String newTaskName = "return books borrowed from Ben to him";
		// boundary case heuristic: the word from and to being present in the title should not cause parsing problems
		String validStartString = "21-02-2015 14:40";
		LocalDateTime validStartTime = CommandParser.parseDateTime(validStartString);
		String validEndString = "21-02-2015 15:00";
		LocalDateTime validEndTime = CommandParser.parseDateTime(validEndString);
		String invalidEndString = "21-13-2015 14:40";
	
			Add valid1 = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + validEndString);
			assertEquals(new Add(new Task(newTaskName, validStartTime, validEndTime, false)), valid1);
	  
	  try {
			CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + invalidEndString);
			fail("exception not thrown");
	  } catch (Exception e) {
  		assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidEndString));
    }
	}
	  
	@Test
	public void testRemoveTaskCommandParsing() throws Exception {
		try {
			CommandParser.getCommandFromInput("remove ");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_REMOVE);
		}
		
		try {
			CommandParser.getCommandFromInput("remove thisIsNotAnInteger");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NUMBER_FORMAT);
		}
		
		try {
			CommandParser.getCommandFromInput("remove 1 2");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_EXPECTED_ONE_TASK_NUM, "remove"));
		}
	
			Command valid1 = CommandParser.getCommandFromInput("remove 1");
			assertEquals(new Remove(1), valid1);
		
	}

	@Test
	public void testUpdateCommandParsing() throws Exception {
		try {
			CommandParser.getCommandFromInput("update");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
		}
		
		try {
			CommandParser.getCommandFromInput("update 1");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
		}
	
		try {
			CommandParser.getCommandFromInput("update thisIsNotAnInteger -end");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NUMBER_FORMAT);
		}
		
		// boundary case heuristic: the word from and to being present in the title should not cause parsing problems
		String newName = "return books borrowed from Ben to him";
		
		String validStartString = "21-02-2015 14:40";
		LocalDateTime validStartTime = CommandParser.parseDateTime(validStartString);
		String validEndString = "21-02-2015 15:00";
		LocalDateTime validEndTime = CommandParser.parseDateTime(validEndString);

		String invalidEndString = "21-13-2015 14:40";
		// declare local constants to avoid fully qualified FIELD_ACTION enum constants, is there a java equivalent of c++'s using keyword?
		DeltaTask.FIELD_ACTION NONE = DeltaTask.FIELD_ACTION.NONE, UPDATE = DeltaTask.FIELD_ACTION.UPDATE, REMOVE = DeltaTask.FIELD_ACTION.REMOVE;
			Update valid1 = (Update)CommandParser.getCommandFromInput("update 1 +name \"" + newName + "\"");
			// If the associated action for a field is NONE, then its new value set in DeltaTask is unimportant as it will never be used 
			assertEquals(valid1.getChanges(), new DeltaTask(UPDATE, newName, NONE, null, NONE, null));

						Update valid2 = (Update)CommandParser.getCommandFromInput("update 1 +end " + validEndString);
						assertEquals(valid2.getChanges(), new DeltaTask(NONE, null, NONE, null, UPDATE, validEndTime));
						
					try {
			CommandParser.getCommandFromInput("update 1 +end " + invalidEndString);
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidEndString));
		}
		
		try {
			CommandParser.getCommandFromInput("update 1 +end");
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "date and time", "+end"));
		}

		try {
			CommandParser.getCommandFromInput("update 1 +start");
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "date and time", "+start"));
		}

		try {
			CommandParser.getCommandFromInput("update 1 +name");
			fail();
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "name", "+name"));
		}
		
			Update valid3 = (Update)CommandParser.getCommandFromInput("update 1 +start " + validStartString);
			assertEquals(valid3.getChanges(), new DeltaTask(NONE, null, UPDATE, validStartTime, NONE, null));

			Update valid4 = (Update)CommandParser.getCommandFromInput("update 1 +name \"" + newName + "\" +start " + validStartString + " +end " + validEndString);
			assertEquals(valid4.getChanges(), new DeltaTask(UPDATE, newName, UPDATE, validStartTime, UPDATE, validEndTime));
			
			Update valid5 = (Update)CommandParser.getCommandFromInput("update 1 -end ");
			assertEquals(valid5.getChanges(), new DeltaTask(NONE, null, NONE, null, REMOVE, null));
			

			Update valid6 = (Update)CommandParser.getCommandFromInput("update 1 -start");
			assertEquals(valid6.getChanges(), new DeltaTask(NONE, null, REMOVE, null, NONE, null));
			
		try {
			CommandParser.getCommandFromInput("update 1 -name");
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_UNRECOGNIZED_UPDATE_TOKEN, "-name"));
		}
		
			Update valid7 = (Update)CommandParser.getCommandFromInput("update 1 -start -end");
			assertEquals(valid7.getChanges(), new DeltaTask(NONE, null, REMOVE, null, REMOVE, null));
	}

	@Test
	public void testDoneParsing() throws Exception {
		try {
			CommandParser.getCommandFromInput("done 1 2");
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_EXPECTED_ONE_TASK_NUM, "mark as completed"));
		}
		
		try {
			CommandParser.getCommandFromInput("done");
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_DONE);
		}
	
			Done valid = (Done)CommandParser.getCommandFromInput("done 1");
			assertEquals(valid, new Done(1));
		
	}
	
	@Test
	public void testUndoParsing() throws Exception {
			Undo valid = (Undo)CommandParser.getCommandFromInput("undo");
			assertEquals(valid, new Undo());
	}

	@Test
	public void testMoveParsing() throws Exception {
		try {
			CommandParser.getCommandFromInput("move");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_MOVE);
		}
		
		String folderPath = "d:/my documents/dropbox/";
			Move valid1 = (Move)CommandParser.getCommandFromInput("move \"" + folderPath + "\"");
			assertEquals(valid1, new Move(folderPath));
		
		// test if backslashes automatically get converted into slashes
			String folderPathUsingBackslashes = folderPath.replace("/", "\\");
		Move valid2 = (Move)CommandParser.getCommandFromInput("move \"" + folderPathUsingBackslashes + "\"");
		assertEquals(valid2, new Move(folderPath));

		try {
			CommandParser.getCommandFromInput("move " + folderPath);
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_FOLDER_PATH_SHOULD_BE_IN_QUOTES);
		}
	}
	
}
