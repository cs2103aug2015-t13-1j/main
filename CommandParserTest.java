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
  public void testAddFloatingTaskCommandParsing() {
    try {
      // boundary case: the only thing entered is the command name with no trailing spaces and further arguments
      // this should throw an exception
      Command invalid = CommandParser.getCommandFromInput("add ");
      fail();
    } catch (Exception e) {
      // assertEquals("Please indicate only one task to add.", e.getMessage());
    }
    
    try {
      // boundary case: nothing is typed before the user presses enter
      // this should throw an exception
      Command invalid = CommandParser.getCommandFromInput("");
      fail();
    } catch (Exception e) {
      // assertEquals("Please indicate only one task to add.", e.getMessage());
    }
    
    String newTaskName = "read To Kill a Mockingbird";
    // Multi-word task names should invalid; task names with more than 1 word must be quoted
    try {
      // this should throw an exception
      Command invalid2 = CommandParser.getCommandFromInput("add " + newTaskName);
      fail();
    } catch (Exception e) {
      // assertEquals("Please indicate only one task to add.", e.getMessage());
    }
    
    try {
      Command c = CommandParser.getCommandFromInput("add \"" + newTaskName + "\"");
      assertEquals(new Add(new Task(newTaskName, false)), c);
    } catch (Exception e) {
      fail();
    }
    
      
  }

  @Test
  public void testAddDeadlineTaskCommandParsing() {
    String newTaskName = "read Harry Potter by J K Rowling";
    // boundary case heuristic: the word to being present in the title should not cause parsing problems
    String validDeadlineString = "21-02-2015 14:40";
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
    String taskNameToRemove = "read The Hobbit";
    // Input like remove read The Hobbit shouis invalid; task names with more than 1 word must be quoted
    try {
      // this should throw an exception
      Command invalid2 = CommandParser.getCommandFromInput("remove " + taskNameToRemove);
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

}
