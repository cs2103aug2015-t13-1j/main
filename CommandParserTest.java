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
	
}
