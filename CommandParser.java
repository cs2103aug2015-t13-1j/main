import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
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
	// error messages for thrown exceptions, public to facilitate testing
	public static final String ERROR_NAME_NOT_IN_QUOTES = "The task name must be enclosed in quotations marks.";
	public static final String ERROR_NOTHING_ENTERED = "Please enter a command.";
	public static final String ERROR_INVALID_QUOTE_COUNT = "There is text inside a quote without a corresponding closing quote, or there are too many quotes.";
	public static final String ERROR_INVALID_COMMAND = "\"%s\" is not a supported command.";
  public static final String ERROR_INCORRECT_ARG_SINGLE = "Please indicate only one task to %s.";
  public static final String ERROR_NUMBER_FORMAT = "Please specify a valid number for the task you want to %s.";
	public static final String ERROR_INCORRECT_ARG_DATE_TIME = "%s is not a date and time in dd-mm-yyyy hh:mm format.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE = "Please specify the task to be updated, and fields to be modified or removed.";
	public static final String ERROR_INVALID_FIELD_TO_UPDATE = "A new %s was not found after %s, or you are trying to perform multiple modifications to that field.";
	public static final String ERROR_INVALID_FIELD_TO_REMOVE = "The %s field could not be removed because you are trying to perform multiple modifications to that field.";
	public static final String ERROR_UNRECOGNIZED_UPDATE_TOKEN = "%s is not a valid update token.";
	
	// positions in the command input
	private static final int POSITION_COMMAND_TYPE = 0;
  private static final int POSITION_FIRST_PARAM = 1;
    
  // the regex pattern to split input by spaces, except if there is a quoted string. 
//   E.g read LOTR in the string '"read LOTR" by 21-02-2015 12:00' is 1 token 
  // because of the way tokenizing is done, an argument like date and time which has a space between them is counted as 2 arguments
	private static final Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	
	// the formatter used to parse date and time
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
	// the maximum number of args for command types that take in arguments
	// the max arguments for add command varies depending on the form used, so it is not listed here
	private static final int MAX_ARG_REMOVE = 1;
	private static final int MAX_ARG_DONE = 1;
	
	// the size of the list of arguments for adding various types of tasks
	private static final int ADD_ARG_SIZE_FOR_FLOATING = 1; // only name argument for floating
	private static final int ADD_ARG_SIZE_FOR_DEADLINE = 4; // name, by keyword, date and time = 4
	private static final int ADD_ARG_SIZE_FOR_EVENT = 7; // name, to, from, and 2 date and times
	
	// positions in the parameter list for the add command
	private static final int POSITION_ADD_NAME = 0;
	private static final int POSITION_ADD_FROM_KEYWORD = 1;
	private static final int POSITION_ADD_BY_KEYWORD = 1;
	private static final int POSITION_ADD_TO_KEYWORD = POSITION_ADD_BY_KEYWORD+3;
	
	// positions in the parameter list for the update command
	private static final int POSITION_UPDATE_INDEX = 0;
	
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
  		throw new Exception(ERROR_NOTHING_ENTERED);
  	}
  	
  	// also check that if quotes are present, there is a corresponding closing quote. There should be either 0 or 2 quotes, depending on command
  	if (isNumberOfQuotesValid(input) == false){
  		throw new Exception(ERROR_INVALID_QUOTE_COUNT);
  	}
  	
  	ArrayList<String> params = splitInput(input);
  	String commandType = getCommandType(params).toLowerCase();
  	ArrayList<String> args = getCommandArgs(params); 
  	
  	switch(commandType) {
    	case "add" :
    		// fall-through
    	case "a":
    		return initAddCommand(args);
    		
    	case "done" :
    		return initDoneCommand(args);
    		
    	case "list" :
    		// fall-through
    	case "l":
    		return initListCommand(args);
    		
    	case "remove" :
    		// fall-through
    	case "r":
    		return initRemoveCommand(args);
    		
    	case "update" :
    		// fall-through
    	case "u":
    		return initUpdateCommand(args);
    		
    	case "undo" :
    		return initUndoCommand();
    		
    	case "help" :
    		return initHelpCommand();
    		
    	case "reformat":
    		return initReformatCommand();
    		
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
  		String param = m.group(1);
	    params.add(param);
  	}
  	
  	return params;
  }
    
  private static String getCommandType(ArrayList<String> params) {
  	assert(params.size() > 0);
    return params.get(POSITION_COMMAND_TYPE);
  }

  private static ArrayList<String> getCommandArgs(ArrayList<String> params) {
    return new ArrayList<String>(params.subList(POSITION_FIRST_PARAM, params.size()));
  }
    
  private static Command initListCommand(ArrayList<String> args) throws Exception {
		if (args.size() == 0) {
			return new List();
		} else {
  		// parse the flags and keywords
  		EnumSet<List.LIST_FLAGS> listFlags = EnumSet.noneOf(List.LIST_FLAGS.class);
  		String[] keywords = null;
  		boolean isAllMarked = false;
  		for (int i = 0; i < args.size(); i++) {
  			String flag = args.get(i);
  			
  			// if "all" flag is marked, remove all flags and break out of the loop
  			if (flag.equals("all")) {
  				listFlags = EnumSet.noneOf(List.LIST_FLAGS.class);
  				isAllMarked = true;
  				break;
  			}
  			
  			switch (flag) {
  				case "floating" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.FLOATING);
  					}
  					break;
  					
  				case "deadline" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.DEADLINE);
  					}
  					break;
  					
  				case "event" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.EVENT);
  					}
  					break;
  					
  				case "done" :
  					// fallthrough
  					
  				case "completed" :
  					// fallthrough
  					
  				case "finished" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.COMPLETED);
  					}
  					break;
  					
  				case "uncompleted" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.UNCOMPLETED);
  					}
  					break;
  					
  				default :
  					// if there are quotation marks, it is a keywords string
  					if (flag.startsWith("\"")) {
  						flag = flag.replace("\"", "");
  						keywords = flag.split(" ");
  					} else {
  						throw new Exception("\"" + flag + "\" is not a recognized marker");
  					}
  					break;
				}
  		}
  		if (keywords == null) {
  			return new List(listFlags);
  		} else {
  			return new List(listFlags, keywords);
  		}
  	}
  }
    
  private static Command initExitCommand() {
  	return new Exit();
  }
  
  private static Command initAddCommand(ArrayList<String> args) throws Exception {
		Task newTask;
		String name = "";
		
		if (args.size() >= 1) {
			name = args.get(POSITION_ADD_NAME); // name is always present in the same position for all tasks
			if (!name.startsWith("\"")) {
				throw new Exception(ERROR_NAME_NOT_IN_QUOTES);
			}
	  		name = name.replace("\"", "");
		}
			
		LocalDateTime endTime, startTime;
		
		switch(determineTaskTypeToBeAdded(args)) {
			case FLOATING :
				newTask = new Task(name, false);
				break;
				
			case DEADLINE :
				// the date and time occurs as 2 words, concat them to be parsed
				String deadline = args.get(POSITION_ADD_BY_KEYWORD+1).concat(" ");
				deadline = deadline.concat(args.get(POSITION_ADD_BY_KEYWORD+2));
				endTime = parseDateTime(deadline);
				
				if (endTime != null) {
					newTask = new Task(name, endTime, false);
				} else {
					throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, deadline));
				}
				break;
				
			case EVENT :
				// the date and time occurs as 2 words, concat them to be parsed
				String start = args.get(POSITION_ADD_FROM_KEYWORD+1).concat(" ");
				start = start.concat(args.get(POSITION_ADD_FROM_KEYWORD+2));
				String end = args.get(POSITION_ADD_TO_KEYWORD+1).concat(" ");
				end = end.concat(args.get(POSITION_ADD_TO_KEYWORD+2));
				startTime = parseDateTime(start);
				endTime = parseDateTime(end);
				
				if (startTime == null) {
					throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, start));
				}
		
				if (endTime == null) {
					throw new Exception(String.format(ERROR_INCORRECT_ARG_DATE_TIME, end));
				}
				// if we get here, all the parameters are correct
				newTask = new Task(name, startTime, endTime, false);
				break;
				
			default :
				throw new Exception("The type of task to be added could not be determined.");
		}
		
		return new Add(newTask);
  }
    
  private static Command initUndoCommand() {
  	// TODO check command length
  	return new Undo();
  }
  
  private static Command initHelpCommand() {
  	return new Help();
  }
  
  private static Command initReformatCommand() {
  	return new Reformat();
  }

  private static TASK_TYPE determineTaskTypeToBeAdded(ArrayList<String> args) {
  	switch(args.size()) {
    	case ADD_ARG_SIZE_FOR_FLOATING:
    		return TASK_TYPE.FLOATING;
    		
    	case ADD_ARG_SIZE_FOR_DEADLINE:
    		boolean isByPresent = args.get(POSITION_ADD_BY_KEYWORD).toLowerCase().equals("by");
    			return isByPresent ? TASK_TYPE.DEADLINE : TASK_TYPE.INVALID;
    		
    	case ADD_ARG_SIZE_FOR_EVENT:
    		boolean isFromPresent = args.get(POSITION_ADD_FROM_KEYWORD).toLowerCase().equals("from");
    		boolean isToPresent= args.get(POSITION_ADD_TO_KEYWORD).toLowerCase().equals("to");
    		return (isFromPresent && isToPresent) ? TASK_TYPE.EVENT : TASK_TYPE.INVALID;
    					
		default :
			return TASK_TYPE.INVALID;
  	}
  }
    
  /*
   *Parses the given string into a LocalDateTime based on the dateAndTimeFormatter string
   *Null is returned on failure
   */
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

  private static Command initDoneCommand(ArrayList<String> args) throws Exception {
  	if (args.size() == 0 || args.size() > MAX_ARG_DONE) {
    	throw new Exception(String.format(ERROR_INCORRECT_ARG_SINGLE, "mark as completed"));    		
  	}
  	
  	try {
  		int taskNum = Integer.parseInt(args.get(0));
    	return new Done(taskNum);
  	} catch (NumberFormatException e) {
  		throw new Exception(String.format(ERROR_NUMBER_FORMAT, "mark as completed"));
  	}
  }
    
  private static Command initUpdateCommand(ArrayList<String> args) throws Exception {
	  boolean isSufficientArguments = args.size() >= 2;
	  if (isSufficientArguments == false) {
	  		throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
	  	}
	  	
  	int taskNumToBeUpdated;
  	
  	try {
  		taskNumToBeUpdated = Integer.parseInt(args.get(POSITION_UPDATE_INDEX));
  	} catch (NumberFormatException e) {
  		throw new Exception(String.format(ERROR_NUMBER_FORMAT, "update"));
  	}
  	
  	DeltaTask changes = getRequestedChanges(args);
  	return new Update(taskNumToBeUpdated, changes);
  }
    
  private static DeltaTask getRequestedChanges(ArrayList<String> params) throws Exception {
  	String newName = null; 
  	LocalDateTime newStart = null, newEnd = null;
  	// boolean flags to prevent multiple modifications of the same field in 1 update command
  	boolean isNameParsed = false, isStartParsed = false, isEndParsed = false; 
  	
  	DeltaTask.FIELD_ACTION nameAction = DeltaTask.FIELD_ACTION.NONE;
  	DeltaTask.FIELD_ACTION startAction = DeltaTask.FIELD_ACTION.NONE;
  	DeltaTask.FIELD_ACTION endAction = DeltaTask.FIELD_ACTION.NONE;
  	
  	for (int i = POSITION_UPDATE_INDEX + 1; i < params.size(); ) {
  		String arg = params.get(i).toLowerCase();
  		switch(arg) {
    		case "+name" :
    			if (isNameParsed == false && (i + 1) < params.size()) {
    				isNameParsed = true;
    				newName = params.get(i + 1);
    				if (!newName.startsWith("\"")) {
    					throw new Exception(ERROR_NAME_NOT_IN_QUOTES);
    				}
    		  		newName = newName.replace("\"", "");
    				nameAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 2; // skip over the new name we just added
    			} else {
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "name", arg));
    			}
    			break;
    			
    		case "+end" :
    			if (isEndParsed == false && (i + 2) < params.size()) {
    				isEndParsed = true;
    				String date = params.get(i + 1);
    				String time = params.get(i + 2);
    				newEnd = parseDateTime(date + " " + time);
    				endAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 3; // skip past 2 words, which is the new date and time 
    			} else {
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg));
    			}
    			break;
  			
    		case "-end" :
    			if (isEndParsed == false) {
    				endAction = DeltaTask.FIELD_ACTION.REMOVE;
    				isEndParsed = true;
    				i++;
    			} else {
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_REMOVE, "end date"));
    			}
    			break;
    			
    		case "+start" :
    			if (isStartParsed == false && (i + 2) < params.size()) {
    				isStartParsed = true;
    				String date = params.get(i + 1);
    				String time = params.get(i + 2);
    				newStart = parseDateTime(date + " " + time);
    				startAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 3; // skip past 2 words, which is the new date and time
    			} else {
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg));
    			}
    			break;
  			
    		case "-start" :
    			if (isStartParsed == false) {
    				startAction = DeltaTask.FIELD_ACTION.REMOVE;
    				isStartParsed = true;
    				i++;
    			} else {
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_REMOVE, "start date"));
    			}
    			break;
    		
  			default :
  				throw new Exception(String.format(ERROR_UNRECOGNIZED_UPDATE_TOKEN, arg));
    	}
  	}
  	
  	return new DeltaTask(nameAction, newName, startAction, newStart, endAction, newEnd);
  }
    
  private static boolean isNumberOfQuotesValid(String input) {
  	int quoteCount = 0;
  	for (int i = 0; i < input.length(); i++) {
  		if (input.charAt(i) == '"') {
  			quoteCount++;
  		}
  	}
  	
  	return quoteCount == 0 || quoteCount == 2;
  }
}

