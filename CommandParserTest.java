import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import org.junit.Test;

//@@author A0126270N
public class CommandParserTest {
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
@Test
	public void testUnsupportedCommandParsing() {
	try {
		Command invalid = CommandParser.getCommandFromInput("abc");
	} catch(Exception e) {
		assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_COMMAND, "abc"));
	}
	}

	@Test
	public void testListCommandParsing() {
		Command c;
		try {
			c = CommandParser.getCommandFromInput("list");
			assertEquals(List.class, c.getClass());
		} catch (Exception e) {
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
			fail("exception was thrown");
		}
	}
	
	@Test
	public void testParsingOfEmptyString() {
		try {
			// boundary case: nothing is typed before the user presses enter
			Command invalid = CommandParser.getCommandFromInput("");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NOTHING_ENTERED);	
		}
		
	}
	
	@Test
	public void testAddFloatingTaskCommandParsing() {
		try {
			// boundary case: the only thing entered is the command name with no trailing spaces and further arguments
			Command invalid = CommandParser.getCommandFromInput("add ");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_ADD);
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
			fail("exception thrown");
		}
		
			
	}

	@Test
	public void testAddDeadlineTaskCommandParsing() throws Exception {
		String newTaskName = "read Harry Potter by J K Rowling";
		// boundary case heuristic: the word to being present in the title should not cause parsing problems
		String validDeadlineString = "21-12-2015 14:40";
		LocalDateTime validDeadline = CommandParser.parseDateTime(validDeadlineString);
		String invalidDeadlineString = "21-13-2015 14:40";
		
try {
			Add validCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + validDeadlineString);
			assertEquals(validCommand.getTask().getEndDateTime(), validDeadline);
			assertEquals(new Add(new Task(newTaskName, validDeadline, false)), validCommand);
} catch (Exception e) {
	fail("exception thrown");
}

try {
			Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by " + invalidDeadlineString);
			fail("exception not thrown");
} catch (Exception e) {
assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidDeadlineString));
}

try {
	// boundary case: the "by" keyword is present without any date
	Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" by ");
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
    		
    try {
    			Add validCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + validEndString);
    			assertEquals(new Add(new Task(newTaskName, validStartTime, validEndTime, false)), validCommand);
    } catch (Exception e) {
    	fail("exception thrown");
    }

    try {
    			Add invalidCommand = (Add) CommandParser.getCommandFromInput("add \"" + newTaskName + "\" from " + validStartString + " to " + invalidEndString);
    			fail("exception not thrown");
    } catch (Exception e) {
assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidEndString));
    }
    		
    	}
    
	@Test
	public void testRemoveTaskCommandParsing() {
		try {
			Command invalid = CommandParser.getCommandFromInput("remove ");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_REMOVE);
		}
		
		try {
			Command invalid = CommandParser.getCommandFromInput("remove thisIsNotAnInteger");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), CommandParser.ERROR_NUMBER_FORMAT);
		}
		
		try {
			Command invalid = CommandParser.getCommandFromInput("remove 1 2");
			fail("exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_EXPECTED_ONE_TASK_NUM, "remove"));
		}
		
		try {
			Command valid = CommandParser.getCommandFromInput("remove 1");
			assertEquals(new Remove(1), valid);
		} catch (Exception e) {
			fail("exception thrown");
		}
		
	}

	@Test
	public void testUpdateCommandParsing() throws Exception {
		try {
		Command invalid = CommandParser.getCommandFromInput("update");
		fail("exception not thrown");
	} catch (Exception e) {
		assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
	}
		
		try {
		Command invalid = CommandParser.getCommandFromInput("update 1");
		fail("exception not thrown");
	} catch (Exception e) {
		assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
	}
	
		try {
			Command invalid = CommandParser.getCommandFromInput("update thisIsNotAnInteger -end");
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
	
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +name \"" + newName + "\"");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(changes.getNewName(), newName);
		} catch(Exception e) {
			fail("exception thrown");
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +end " + validEndString);
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(changes.getNewEnd(), validEndTime);
		} catch(Exception e) {
			fail("exception thrown");
		}

		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 +end " + invalidEndString);
			fail("exception not thrown");
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_DATE_AND_TIME, invalidEndString));
		}
		
		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 +end");
fail();
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "date and time", "+end"));
		}

		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 +start");
fail();
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "date and time", "+start"));
		}

		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 +name");
fail();
		} catch(Exception e) {
			assertEquals(e.getMessage(), String.format(CommandParser.ERROR_INVALID_FIELD_TO_UPDATE, "name", "+name"));
		}

		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 +start " + validStartString);
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.UPDATE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(changes.getNewStart(), validStartTime);
		} catch(Exception e) {
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
		} catch(Exception e) {
			fail("Exception thrown");
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -end ");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
		} catch(Exception e) {
			fail("exception thrown");
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -start");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.NONE);
			assertEquals(nameAction, DeltaTask.FIELD_ACTION.NONE);
		} catch(Exception e) {
			fail("exception thrown");
		}
		
		try {
			Update invalid = (Update)CommandParser.getCommandFromInput("update 1 -name");
		fail();
		} catch(Exception e) {
assertEquals(e.getMessage(), String.format(CommandParser.ERROR_UNRECOGNIZED_UPDATE_TOKEN, "-name"));
		}
		
		try {
			Update valid = (Update)CommandParser.getCommandFromInput("update 1 -start -end");
			DeltaTask changes = valid.getChanges();
			DeltaTask.FIELD_ACTION startAction = changes.getStartAction(), endAction = changes.getEndAction(), nameAction = changes.getNameAction();
			assertEquals(startAction, DeltaTask.FIELD_ACTION.REMOVE);
			assertEquals(endAction, DeltaTask.FIELD_ACTION.REMOVE);
		} catch(Exception e) {
			fail("exception thrown");
		}
		
	}

	@Test
	public void testDoneParsing() {
		try {
			Command invalid = CommandParser.getCommandFromInput("done 1 2");
			fail("exception not thrown");
		} catch(Exception e) {
				assertEquals(e.getMessage(), String.format(CommandParser.ERROR_EXPECTED_ONE_TASK_NUM, "mark as completed"));
			}
		
		try {
			Command invalid = CommandParser.getCommandFromInput("done");
			fail("exception not thrown");
		} catch(Exception e) {
				assertEquals(e.getMessage(), CommandParser.ERROR_INSUFFICIENT_ARGUMENTS_FOR_DONE);
			}
		
		try {
			Done valid = (Done)CommandParser.getCommandFromInput("done 1");
			assertEquals(valid, new Done(1));
		} catch(Exception e) {
			fail("exception thrown");
			}
		
	}
}
