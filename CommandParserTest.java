import static org.junit.Assert.*;

import org.junit.Test;

public class CommandParserTest {

/*	@Test
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
	public void testAddFloatingTaskCommandParsing() {
		try {
			// this should throw an exception
			Command invalid1 = CommandParser.getCommandFromInput("add ");
			fail();
		} catch (Exception e) {
			assertEquals("Please indicate only one task to add.", e.getMessage());
		}
		String newTaskName = "read The Hobbit";
		// Input like add read The Hobbit should invalid; task names with more than 1 word must be quoted
		try {
			// this should throw an exception
			Command invalid2 = CommandParser.getCommandFromInput("add " + newTaskName);
			fail();
		} catch (Exception e) {
			assertEquals("Please indicate only one task to add.", e.getMessage());
		}
		try {
			Command c = CommandParser.getCommandFromInput("add \"" + newTaskName + "\"");
			assertEquals(new Add(new Task(newTaskName)), c);
		} catch (Exception e) {
			fail();
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
		String taskNameToRemove = "read The Hobbit";
		// Input like remove read The Hobbit shouis invalid; task names with more than 1 word must be quoted
		try {
			// this should throw an exception
			Command invalid2 = CommandParser.getCommandFromInput("remove " + taskNameToRemove);
			fail();
		} catch (Exception e) {
			assertEquals("Please indicate only one task to remove.", e.getMessage());
		}
		
		try {
			Command c = CommandParser.getCommandFromInput("remove \"" + taskNameToRemove + "\"");
			assertEquals(new Remove(new Task(taskNameToRemove)), c);
		} catch (Exception e) {
			fail();
		}
	}

}
