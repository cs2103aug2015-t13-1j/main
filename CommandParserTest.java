import static org.junit.Assert.*;

import org.junit.Test;

public class CommandParserTest {

	@Test
	public void testInvalidCommandParsing() {
		Command c = CommandParser.getCommandFromInput("abc");
		assertEquals(Command.Type.INVALID, c.getCommandType());
	}

	@Test
	public void testListCommandParsing() {
		Command c = CommandParser.getCommandFromInput("list");
		assertEquals(Command.Type.LIST, c.getCommandType());
	}
	
	@Test
	public void testExitCommandParsing() {
		Command c1 = CommandParser.getCommandFromInput("exit");
		assertEquals(Command.Type.EXIT, c1.getCommandType());
		Command c2 = CommandParser.getCommandFromInput("Quit");
		assertEquals(Command.Type.EXIT, c2.getCommandType());
	}
	
	@Test
	public void testAddFloatingTaskCommandParsing() {
		Command invalid1 = CommandParser.getCommandFromInput("add ");
		assertEquals(Command.Type.INVALID, invalid1.getCommandType());
		String newTaskName = "read The Hobbit";
		// Input like add read The Hobbit shouis invalid; task names with more than 1 word must be quoted
		Command invalid2 = CommandParser.getCommandFromInput("add " + newTaskName);
		assertEquals(Command.Type.INVALID, invalid2.getCommandType());
		
		Command c = CommandParser.getCommandFromInput("add \"" + newTaskName + "\"");
		assertEquals(Command.Type.ADD, c.getCommandType());
		Task t = c.getCommandTask();
		assertEquals(newTaskName, t.getName());
	}

	@Test
	public void testRemoveFloatingTaskCommandParsing() {
		Command invalid1 = CommandParser.getCommandFromInput("remove ");
		assertEquals(Command.Type.INVALID, invalid1.getCommandType());
		String taskNameToRemove = "read The Hobbit";
		// Input like remove read The Hobbit shouis invalid; task names with more than 1 word must be quoted
		Command invalid2 = CommandParser.getCommandFromInput("remove " + taskNameToRemove);
		assertEquals(Command.Type.INVALID, invalid2.getCommandType());
		
		Command c = CommandParser.getCommandFromInput("remove \"" + taskNameToRemove + "\"");
		assertEquals(Command.Type.REMOVE, c.getCommandType());
		Task t = c.getCommandTask();
		assertEquals(taskNameToRemove, t.getName());
	}

}
