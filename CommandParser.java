import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandParser parses user's input to create Command objects that have the
 * appropriate fields initialised. For example, the "remove" command requires the
 * taskName field to be initialised.
 * 
 * @author Dickson
 *
 */

public class CommandParser {
	// error messages for thrown exceptions
	private static final String ERROR_INVALID_COMMAND = "\"%s\" is not a supported command.";
    private static final String ERROR_INCORRECT_ARG_SINGLE = "Please indicate only one task to %s.";
    private static final String ERROR_NUMBER_FORMAT = "Please specify the number of the task you want to %s.";
	private static final String ERROR_INCORRECT_ARG_UPDATE = "Please indicate the task you want to change "
																+ "and which values to change.";
	private static final String ERROR_INCORRECT_ARG_DATE_TIME = "%s is not a date and time in dd-mm-yyyy hh:mm format.";
	// positions in the command input
	private static final int POSITION_COMMAND_TYPE = 0;
    private static final int POSITION_FIRST_PARAM = 1;
    
    // the regex pattern to split input by spaces, except if there is a quoted string
    // because of the way tokenizing is done, an argument like date and time which has a space between them is counted as 2 arguments
	private static final Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
// the formatter used to parse date and time
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	// the maximum number of args for command types that take in arguments
	
	private static final int MAX_ARG_REMOVE = 1;
	// positions in the parameter list for the add command
	private static final int POSITION_ADD_NAME = 0;
	private static final int POSITION_ADD_FROM_KEYWORD = 1;
	private static final int POSITION_ADD_BY_KEYWORD = 1;
	private static final int POSITION_ADD_TO_KEYWORD = POSITION_ADD_BY_KEYWORD+3;
	// positions in the parameter list for the update command
	private static final int POSITION_UPDATE_NEW = 1;
	private static final int POSITION_UPDATE_OLD = 0;
// used for the add command to determine what type of task is to be added
	private enum TASK_TYPE {
		FLOATING, DEADLINE, EVENT, INVALID
	}
	
	/**
	 * Parse the input into the appropriate command
	 * 
	 * @param input			the input to parse
	 * @return				the command parsed from the input
	 * @throws Exception	if there are an incorrect number of arguments for a given command
	 * 						or if the command is unrecognized
	 */
    public static Command getCommandFromInput(String input) throws Exception {
    	assert(input != null);
    	// exit early if this is an empty string (which happens when the user types nothing before pressing enter)
    	// this requires special handling because attempting to split this empty string into params causes a size 0 array
    	if (input.equals("")) {
    		throw new Exception("Please enter a command.");
    	}
    	
    	ArrayList<String> params = splitInput(input);
    	String commandType = getCommandType(params).toLowerCase();
    	ArrayList<String> args     	= getCommandArgs(params); 
    	
    	switch(commandType) {
	    	case "add" :
	    		return initAddCommand(args);
	    		
	    	case "list" :
	    		return initListCommand();
	    		
	    	case "remove" :
	    		return initRemoveCommand(args);
	    		
	    	case "update" :
	    		return initUpdateCommand(args);
	    		
	    	case "exit" :
	    		// fallthrough
	    		
	    	case "quit" :
	    		return initExitCommand();
	    		
	    	default :
	    		throw new Exception(String.format(ERROR_INVALID_COMMAND, commandType));
    	}
    }
    
    private static ArrayList<String> splitInput(String input) {
    	ArrayList<String> params = new ArrayList<String>();
    	Matcher m = splitter.matcher(input);
    	
    	while(m.find()) {
    		String param = m.group(1).replace("\"", "");
    	    params.add(param);
    	}
    	
    	return params;
    }
    
    private static String getCommandType(ArrayList<String> params) {
    	assert(params.size() > 0);
        return params.get(POSITION_COMMAND_TYPE);
    }

    private static ArrayList<String> getCommandArgs(ArrayList<String> params) {
        return new ArrayList<String>(params.subList(POSITION_FIRST_PARAM,
                                                        params.size()));
    }
    
    private static Command initListCommand() {
    	// TODO handle option for List command with search parameters
    	return new List();
    }
    
    private static Command initExitCommand() {
    	return new Exit();
    }
    
    private static Command initAddCommand(ArrayList<String> args) throws Exception {
		
		Task newTask;
		String name = "";
		if (args.size() >= 1) {
			name = args.get(POSITION_ADD_NAME); // name is always present in the same position for all tasks
		}
			
		switch(determineTaskTypeToBeAdded(args)) {
		case FLOATING:
			newTask = new Task(name, false);
			break;
		case DEADLINE:
		{
			// the date and time occurs as 2 words, concat them to be parsed
			String deadline = args.get(POSITION_ADD_BY_KEYWORD+1).concat(" ");
			deadline = deadline.concat(args.get(POSITION_ADD_BY_KEYWORD+2));
			LocalDateTime endTime = parseDateTime(deadline);
			
			if (endTime != null) {
				newTask = new Task(name, endTime, false);
			}
			else {
				throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, deadline));
			}
			break;
		}
		case EVENT:
			// the date and time occurs as 2 words, concat them to be parsed
						String start = args.get(POSITION_ADD_FROM_KEYWORD+1).concat(" ");
						start = start.concat(args.get(POSITION_ADD_FROM_KEYWORD+2));
						String end = args.get(POSITION_ADD_TO_KEYWORD+1).concat(" ");
						end = end.concat(args.get(POSITION_ADD_TO_KEYWORD+2));
						LocalDateTime startTime = parseDateTime(start);
						LocalDateTime endTime = parseDateTime(end);
						
						if (startTime == null) {
							throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, start));
						}

						if (endTime == null) {
							throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, end));
						}
// if we get here, all the parameters are correct
							newTask = new Task(name, startTime, endTime, false);
						break;
			default:
				throw new Exception("The type of task to be added could not be determined.");
		}
		
		return new Add(newTask);
    }

    private static TASK_TYPE determineTaskTypeToBeAdded(ArrayList<String> args) {
    	switch(args.size()) {
    	case 1: // floating tasks have only 1 argument, the name
    		return TASK_TYPE.FLOATING;
    	case 4: // name, by keyword, date and time = 4 args
    		if (args.get(POSITION_ADD_BY_KEYWORD).toLowerCase().equals("by")) {
    			return TASK_TYPE.DEADLINE;
    		}
    		else {
    			return TASK_TYPE.INVALID;
    		}
    	case 7: // name, from, to, and 2 dates + 2 times
    		boolean isFromPresent = args.get(POSITION_ADD_FROM_KEYWORD).toLowerCase().equals("from");
    		boolean isToPresent= args.get(POSITION_ADD_TO_KEYWORD).toLowerCase().equals("to");
    		if (isFromPresent && isToPresent) {
    			return TASK_TYPE.EVENT;
    		} else {
    			return TASK_TYPE.INVALID;
    		}
    			
default:
	return TASK_TYPE.INVALID;
    	}
    }
    
    private static LocalDateTime parseDateTime(String dateTimeString) {
    	try {
    	LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
    	return dateTime;
    	} 
    	catch(DateTimeParseException e) {
    	return null;	
    	}
    	
    }
    private static Command initRemoveCommand(ArrayList<String> args) throws Exception {
    	if (args.size() == 0 || args.size() > MAX_ARG_REMOVE) {
	    	throw new Exception(String.format(ERROR_INCORRECT_ARG_SINGLE, "remove"));    		
    	}
    	
    	try {
        	return new Remove(Integer.parseInt(args.get(0)));
    	} catch (NumberFormatException e) {
    		throw new Exception(String.format(ERROR_NUMBER_FORMAT, "remove"));
    	}
    }


    private static Command initUpdateCommand(ArrayList<String> args) throws Exception {
    	if (args.size() != 2) {    	
	    	throw new Exception(ERROR_INCORRECT_ARG_UPDATE);
	    }
    	
    	try {
        	Task newTask = new Task(args.get(POSITION_UPDATE_NEW), false);
    		return new Update(Integer.parseInt(args.get(POSITION_UPDATE_OLD)), newTask);
    	} catch (NumberFormatException e) {
    		throw new Exception(String.format(ERROR_NUMBER_FORMAT, "update"));
    	}
    }
}

