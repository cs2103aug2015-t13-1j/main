import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import org.junit.Test;


public class CommandParserTest {
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
/*	
@Test
	public void testInvalidCommandParsing() {
		Command c = CommandParser.getCommandFromInput("abc");
		assertEquals(Command.Type.INVALID, c.getCommandType());
	}
*/
	
	@Test
	public void testListCommandParsing() {
		Command c;
		try {
			c = CommandParser.getCommandFromInput("list");
			assertEquals(List.class, c.getClass());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	public void testExitCommandParsing() {
		Command c1;
		try {
			c1 = CommandParser.getCommandFromInput("exit");
			assertEquals(Exit.class, c1.getClass());
			Command c2 = CommandParser.getCommandFromInput("Quit");
			assertEquals(Exit.class, c2.getClass());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail();
		}
	}
	
	@Test
	public void testParsingOfEmptyString() {
		try {
			// boundary case: nothing is typed before the user presses enter
			// this should throw an exception
			Command invalid = CommandParser.getCommandFromInput("");
			fail();
		} catch (Exception e) {
			
		}
		
	}
	
	@Test
	public void testAddFloatingTaskCommandParsing() {
		try {
			// boundary case: the only thing entered is the command name with no trailing spaces and further arguments
			// this should throw an exception
			Command invalid = CommandParser.getCommandFromInput("add ");
			fail();
		} catch (Exception e) {
			
		}
		
		
		String newTaskName = "read To Kill a Mockingbird";
		// Multi-word task names should invalid; task names with more than 1 word must be quoted
		try {
			// this should throw an exception
			Command invalid2 = CommandParser.getCommandFromInput("add " + newTaskName);
			fail();
		} catch (Exception e) {
			
		}
		
		try {
			Command valid = CommandParser.getCommandFromInput("add \"" + newTaskName + "\"");
			assertEquals(new Add(new Task(newTaskName, false)), valid);
		} catch (Exception e) {
			fail();
		}
		
			
	}

	@Test
	public void testAddDeadlineTaskCommandParsing() {
		String newTaskName = "read Harry Potter by J K Rowling";
		// boundary case heuristic: the word to being present in the title should not cause parsing problems
		String validDeadlineString = "21-12-2015 14:40";
		LocalDateTime validDeadline = parseDateTime(validDeadlineString);
		String invalidDeadlineString = "21-13-2015 14:40";
		LocalDateTime invalidDeadline = parseDateTime(invalidDeadlineString);
		
try {
			Add validCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + validDeadlineString);
			assertEquals(validCommand.getTask().getEndDateTime(), validDeadline);
			assertEquals(new Add(new Task(newTaskName, validDeadline, false)), validCommand);
} catch (Exception e) {
	fail();
}

try {
			Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + invalidDeadlineString);
			fail();
} catch (Exception e) {

}

try {
	// boundary case: the "by" keyword is present without any date
	Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by ");
	fail();
} catch (Exception e) {

}

	}
	
	private LocalDateTime parseDateTime(String dateTimeString) {
    	try {
    	LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
    	return dateTime;
    	} 
    	catch(DateTimeParseException e) {
    	return null;	
    	}
	}
	
    	@Test
    	public void testAddEventTaskCommandParsing() {
    		String newTaskName = "return books borrowed from Ben to him";
    		// boundary case heuristic: the word from and to being present in the title should not cause parsing problems
    		String validStartString = "21-02-2015 14:40";
    		LocalDateTime validStartTime = parseDateTime(validStartString);
    		String validEndString = "21-02-2015 15:00";
    		LocalDateTime validEndTime = parseDateTime(validEndString);

    		String invalidEndString = "21-13-2015 14:40";
    		LocalDateTime invalidEndTime = parseDateTime(invalidEndString);
    		
    try {
    			Add validCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + validEndString);
    			assertEquals(new Add(new Task(newTaskName, validStartTime, validEndTime, false)), validCommand);
    } catch (Exception e) {
    	fail();
    }

    try {
    			Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + invalidEndString);
    			fail(); // the above line should trigger an exception
    } catch (Exception e) {

    }
    		
    	}
    
	@Test
	public void testRemoveFloatingTaskCommandParsing() {
		try {
			// this should throw an exception
			Command invalid1 = CommandParser.getCommandFromInput("remove ");
			fail();
		} catch (Exception e) {
			assertEquals("Please indicate only one task to remove.", e.getMessage());
		}
		
		/*
		try {
			Command c = CommandParser.getCommandFromInput("remove 1");
			assertEquals(new Remove(1), c);
		} catch (Exception e) {
			fail();
		}*/
		
	}

	@Test
	public void testUpdateCommandParsing() {
		try {
		Command invalid = CommandParser.getCommandFromInput("update");
		fail();
	} catch (Exception e) {
		// we should get an exception
	}
		
		try {
		Command invalid = CommandParser.getCommandFromInput("update 1");
		fail();
	} catch (Exception e) {
		
	}
		
		// boundary case heuristic: the word from and to being present in the title should not cause parsing problems
		String newName = "return books borrowed from Ben to him";
		
		String validStartString = "21-02-2015 14:40";
		LocalDateTime validStartTime = parseDateTime(validStartString);
		String validEndString = "21-02-2015 15:00";
		LocalDateTime validEndTime = parseDateTime(validEndString);

		String invalidEndString = "21-13-2015 14:40";
		LocalDateTime invalidEndTime = parseDateTime(invalidEndString);

		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +name \"" + newName + "\"");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(changes.getNewName(), newName);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +end " + validEndString);
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(changes.getNewEnd(), validEndTime);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +start " + validStartString);
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(changes.getNewStart(), validStartTime);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +name \"" + newName + "\" +start " + validStartString + " +end " + validEndString);
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(changes.getNewStart(), validStartTime);
			assertEquals(changes.getNewEnd(), validEndTime);
			assertEquals(changes.getNewName(), newName);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -end ");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -start");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
		} catch(Exception E) {
			fail();
		}
		
		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 -name");
		fail();
		} catch(Exception E) {
// we should get an exception
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -start -end");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.REMOVE);
		} catch(Exception E) {
			fail();
		}
		
	}
	
}
