//@@author A0126270N
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	public static final String ERROR_NOTHING_ENTERED = "Please enter a command.";
	public static final String ERROR_INVALID_QUOTE_COUNT = "There is text inside a quote without a corresponding closing quote, or there are too many quotes.";
	public static final String ERROR_INVALID_COMMAND = "\"%s\" is not a supported command.";
  public static final String ERROR_EXPECTED_ONE_TASK_NUM = "Please indicate only one task to %s.";
  public static final String ERROR_NUMBER_FORMAT = "Please specify a valid task number.";
	public static final String ERROR_INVALID_DATE_AND_TIME = "%s is not a date and time in dd-mm hh:mm or dd-mm-yyyy hh:mm format, where hh:mm is in 24-hour time.";
	public static final String ERROR_COULD_NOT_DETERMINE_TASK_TYPE_TO_ADD = "The type of task to be added could not be determined.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_ADD = "Please specify the name for the new task, and its start and end date and time if appropriate.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_REMOVE = "Please specify the task number to be removed.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_DONE = "Please specify the task number to be marked completed.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE = "Please specify the task to be updated, and fields to be modified or removed.";
	public static final String ERROR_INSUFFICIENT_ARGUMENTS_FOR_MOVE = "Please specify the path (enclosed in quotes) to the folder that should be used for task storage.";
	public static final String ERROR_INVALID_FIELD_TO_UPDATE = "A new %s was not found after %s, or you are trying to perform multiple modifications to that field.";
	public static final String ERROR_INVALID_FIELD_TO_REMOVE = "The %s field could not be removed because you are trying to perform multiple modifications to that field.";
	public static final String ERROR_UNRECOGNIZED_UPDATE_TOKEN = "%s is not a valid update token.";
	public static final String ERROR_NAME_SHOULD_BE_IN_QUOTES = "The task name should be surrounded by quotes.";
	public static final String ERROR_NAME_SHOULD_CONTAIN_NON_WHITESPACE_CHARS = "The task name should not be composed entirely of spaces.";
	public static final String ERROR_FOLDER_PATH_SHOULD_BE_IN_QUOTES = "The folder path should be surrounded by quotes.";
	public static final String ERROR_FOLDER_PATH_TOO_SHORT = "The folder path should contain at least 1 character surrounded by quotes.";
	
	// positions in the command input
	private static final int POSITION_COMMAND_TYPE = 0;
  private static final int POSITION_FIRST_PARAM = 1;
    
  // the regex pattern to split input by spaces, except if there is a quoted string. 
//   E.g read LOTR in the string '"read LOTR" by 21-02-2015 12:00' is 1 token 
  // because of the way tokenizing is done, an argument like date and time which has a space between them is counted as 2 arguments
	private static final Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	
	// the default formatter used to parse date and time
	private static final DateTimeFormatter primaryDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	// if user omits year, we assume they mean the current year 
	// to implement this, we append the current year to what the user entered, and so we need another formatter
	private static final DateTimeFormatter secondaryDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM HH:mm yyyy");
	// the maximum number of args for command types that take in arguments
	// the max arguments for add command varies depending on the form used, so it is not listed here
	private static final int MAX_ARG_REMOVE = 1;
	private static final int MAX_ARG_DONE = 1;
	
	// the size of the list of arguments for adding various types of tasks
	private static final int ADD_ARG_SIZE_FOR_UNSCHEDULED = 1; // only name argument for unscheduled
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
		UNSCHEDULED, DEADLINE, EVENT, INVALID
	}
	
	private static final String HELP_ADD = "add";
	private static final String HELP_REMOVE = "remove";
	private static final String HELP_LIST = "list";
	private static final String HELP_INVALID = "invalid";

	private static final Logger log = Logger.getLogger(Ui.LOG_NAME);
	
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
  	log.log(Level.INFO, "parsing input \"" + input + "\"\n");
  	// exit early if this is an empty string (which happens when the user types nothing before pressing enter)
  	// this requires special handling because attempting to split this empty string into params causes a size 0 array
  	if (input.equals("")) {
  		log.log(Level.INFO, "aborting parsing because input is an empty string\n");
  		throw new Exception(ERROR_NOTHING_ENTERED);
  	}
  	
  	// also check that if quotes are present, there is a corresponding closing quote. There should be either 0 or 2 quotes, depending on command
  	// this is an important check, because it simplifies parsing for more complex command formats
  	int nQuotes = countQuotesInString(input);
  	if (!(nQuotes == 0 || nQuotes == 2)) {
  		log.log(Level.INFO, "aborting parsing because number of quotes not 0 or 2\n");
  		throw new Exception(ERROR_INVALID_QUOTE_COUNT);
  	}
  	
  	ArrayList<String> params = splitInput(input);
  	log.log(Level.INFO, "params after splitting: " + params + "\n");
  	String commandType = getCommandType(params).toLowerCase();
  	ArrayList<String> args = getCommandArgs(params); 
  	
  	switch(commandType) {
    	case "add" :
    		// fall-through
    	case "a":
    		return initAddCommand(args);
    		
    	case "done" :
    		// fall-through
    	case "d":
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
    		// fall-through
    	case "h":
    		return initHelpCommand(args);
    		
    	case "clear":
    		return initClearCommand();
    		
    	case "move":
    		// fall-through
    	case "m":
    		return initMoveCommand(args);
    		
    	case "exit" :
    		// fall-through
    	case "quit" :
    		return initExitCommand();
    		
    	default :
    		log.log(Level.INFO, "Could not determine command type, " + commandType + " is an unsupported command\n");
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
    
  //@@author A0145732H
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
  			switch (flag) {
  				case "unscheduled" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.UNSCHEDULED);
  					}
  					break;
  					
  				case "deadlines" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.DEADLINE);
  					}
  					break;
  					
  				case "events" :
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
  					
  				case "today" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.TODAY);
  					}
  					break;  					
  					
  				case "tomorrow" :
  					if (!isAllMarked) {
  						listFlags.add(List.LIST_FLAGS.TOMORROW);
  					}
  					break;  					
  					
  				default :
  					// if there are quotation marks, it is a keywords string
  					if (flag.startsWith("\"")) {
  						flag = flag.replace("\"", "");
  						keywords = flag.split(" ");
  					} else {
  						throw new Exception("\"" + flag + "\" is not a recognized keyword");
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
    
  //@@author A0126270N
  private static Command initExitCommand() {
  	return new Exit();
  }
  
  private static Command initAddCommand(ArrayList<String> args) throws Exception {
	  if (args.size() == 0) {
		  log.log(Level.INFO, "Insufficient arguments for add");
		  throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_ADD);
	  }
	  
		Task newTask;
		String name = args.get(POSITION_ADD_NAME); // name is always present in the same position for all tasks
		log.log(Level.INFO, "Name entered = " + name + "\n");
			verifyTaskNameValidity(name);
			name = name.replace("\"", "").trim();
			log.log(Level.INFO, "Name verified\n");
		LocalDateTime endTime, startTime;
		
		switch(determineTaskTypeToBeAdded(args)) {
			case UNSCHEDULED :
				newTask = new Task(name, false);
				log.log(Level.INFO, "created unscheduled task with name " + name + "\n");
				break;
				
			case DEADLINE :
				// the date and time occurs as 2 words, concat them to be parsed
				String deadline = args.get(POSITION_ADD_BY_KEYWORD+1).concat(" ");
				deadline = deadline.concat(args.get(POSITION_ADD_BY_KEYWORD+2));
				endTime = parseDateTime(deadline);
				
					newTask = new Task(name, endTime, false);
					log.log(Level.INFO, "creating deadline with name " + name + " , end date" + endTime + "\n");
				break;
				
			case EVENT :
				// the date and time occurs as 2 words, concat them to be parsed
				String start = args.get(POSITION_ADD_FROM_KEYWORD+1).concat(" ");
				start = start.concat(args.get(POSITION_ADD_FROM_KEYWORD+2));
				String end = args.get(POSITION_ADD_TO_KEYWORD+1).concat(" ");
				end = end.concat(args.get(POSITION_ADD_TO_KEYWORD+2));
				startTime = parseDateTime(start);
				endTime = parseDateTime(end);
				
				newTask = new Task(name, startTime, endTime, false);
				log.log(Level.INFO, "creating event with name " + name + " , start = " + startTime + ", end = " + endTime + "\n");
				break;
				
			default :
				log.log(Level.INFO, "Aborting, cannot determine the type of task to add\n");
				throw new Exception(ERROR_COULD_NOT_DETERMINE_TASK_TYPE_TO_ADD);
		}
		
		return new Add(newTask);
  }
    
  private static Command initUndoCommand() {
  	return new Undo();
  }
  
  //@@author A0100081E
  private static Command initHelpCommand(ArrayList<String> args) throws Exception {
  	String helpType;
  	
  	if (args.size() == 0) {
			return new Help();
		} else {
			
			helpType = determineHelpTypeToBeList(args);
					
			if (helpType != HELP_INVALID) {
				return new Help(determineHelpTypeToBeList(args));
			} else {
				log.log(Level.INFO, "Aborting, could not determine type of help to show\n");
				throw new Exception("The type of help to be shown could not be determined.");
			}
		}
  }
  
  private static Command initClearCommand() {
  	return new Clear();
  }

//@@author A0126270N
  private static Command initMoveCommand(ArrayList<String> args) throws Exception {
	  if (args.size() == 0) {
		  log.log(Level.INFO, "aborting as no folder path was specified.\n");
		  throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_MOVE);
	  }
	  
  	String fileLocation = args.get(0);
  	log.log(Level.INFO, "folder path entered = " + fileLocation + "\n");
  	
  	if (fileLocation.length() <= 2) {
  		log.log(Level.INFO, "aborting as folder path is too short\n");
  		throw new Exception(ERROR_FOLDER_PATH_TOO_SHORT);
  	}
  	
  	char first = fileLocation.charAt(0), last = fileLocation.charAt(fileLocation.length()-1);
  	if (first != '"' || last != '"') {
  		log.log(Level.INFO, "aborting as folder path is not in quotes\n");
  		throw new Exception(ERROR_FOLDER_PATH_SHOULD_BE_IN_QUOTES);
  	}
  	
  	String fileLocationWithoutQuotes = fileLocation.substring(1, fileLocation.length() - 1);
  	// it is safe to use slash (but not backslash) in folder paths for java, so replace any backslashes
  	fileLocationWithoutQuotes = fileLocationWithoutQuotes.replace("\\", "/");
  	char end = fileLocationWithoutQuotes.charAt(fileLocationWithoutQuotes.length()-1);

  	if (end != '/') {
  		fileLocationWithoutQuotes = fileLocationWithoutQuotes + "/";
  		log.log(Level.INFO, "appending slash to the end of file location, location = " + fileLocationWithoutQuotes + "\n");
		}
  	  	
  	return new Move(fileLocationWithoutQuotes);
  }

  private static TASK_TYPE determineTaskTypeToBeAdded(ArrayList<String> args) {
  	switch(args.size()) {
    	case ADD_ARG_SIZE_FOR_UNSCHEDULED:
    		return TASK_TYPE.UNSCHEDULED;
    		
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
  
  //@@author A0100081E
  private static String determineHelpTypeToBeList(ArrayList<String> args) {	
  	switch (args.get(0).toLowerCase()) {
  		case HELP_ADD:
  				return HELP_ADD;
  		case HELP_LIST:
				return HELP_LIST;
  		case HELP_REMOVE:
				return HELP_REMOVE;
  		default :
  			return HELP_INVALID;
  	}
  }
    
  //@@author A0126270N
  /*
   *Parses the given string into a LocalDateTime based on the primary or secondary formatter strings
   *An exception is thrown if there was an error parsing the String 
   */
  public static LocalDateTime parseDateTime(String dateTimeString) throws Exception {
  	try {
    	LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, primaryDateTimeFormatter);
    	return dateTime;
  	}
  	catch(DateTimeParseException e) {
  		// deliberately do nothing and try using another formatter
  	}
  	
  	// if the user has the year omited, assume that it is the current year, and try using secondary formatter
  	try {
  		int currentYear = Calendar.getInstance ().get(Calendar.YEAR);
  		LocalDateTime dateTime = LocalDateTime.parse(dateTimeString + " " + currentYear, secondaryDateTimeFormatter);
  		return dateTime;
  	}
    	catch(DateTimeParseException e) {
    		log.log(Level.INFO, "aborting, could not parse " + dateTimeString + " with the given formatter\n");
    		throw new Exception(String.format(ERROR_INVALID_DATE_AND_TIME, dateTimeString));	
  	}
  	
  }
    
  /*
   * Converts the integer represented by this String into an integer
   *An exception is thrown if a parsing error error was encountered 
   */
  private static int parseInt(String integerString) throws Exception {
	  try {
	  return Integer.parseInt(integerString);
	  } catch (NumberFormatException e) {
		  log.log(Level.INFO, "Aborting, could not parse " + integerString + " as an integer\n");
	  		throw new Exception(ERROR_NUMBER_FORMAT);
  }
  }
  
  private static Command initRemoveCommand(ArrayList<String> args) throws Exception {
  	if (args.size() == 0) {
  		log.log(Level.INFO, "aborting, insufficient arguments\n");
  		throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_REMOVE);
  	}
  	
  		if (args.size() > MAX_ARG_REMOVE) {
  			log.log(Level.INFO, "aborting, too many arguments\n");
    	throw new Exception(String.format(ERROR_EXPECTED_ONE_TASK_NUM, "remove"));    		
  	}
  
    	return new Remove(parseInt(args.get(0)));
  }

  private static Command initDoneCommand(ArrayList<String> args) throws Exception {
  	if (args.size() == 0) {
  		log.log(Level.INFO, "aborting, insufficient arguments\n");
  		throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_DONE);
  	}
  			
  			if (args.size() > MAX_ARG_DONE) {
  		  		log.log(Level.INFO, "aborting, too many arguments\n");
    	throw new Exception(String.format(ERROR_EXPECTED_ONE_TASK_NUM, "mark as completed"));    		
  	}
  	
  		int taskNum = parseInt(args.get(0));
    	return new Done(taskNum);
  }
    
  private static Command initUpdateCommand(ArrayList<String> args) throws Exception {
	  boolean isSufficientArguments = args.size() >= 2;
	  if (isSufficientArguments == false) {
	  		log.log(Level.INFO, "aborting, insufficient arguments\n");
	  		throw new Exception(ERROR_INSUFFICIENT_ARGUMENTS_FOR_UPDATE);
	  	}
	  	
  	int taskNumToBeUpdated = parseInt(args.get(POSITION_UPDATE_INDEX));
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
    		  		log.log(Level.INFO, "+name detected\n");
    				isNameParsed = true;
    				newName = params.get(i + 1);
    				verifyTaskNameValidity(newName);
    				newName = newName.replace("\"", "").trim();
    				nameAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 2; // skip over the new name we just added
    			} else {
    		  		log.log(Level.INFO, "aborting " + String.format(ERROR_INVALID_FIELD_TO_UPDATE, "name", arg) + "\n");
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "name", arg));
    			}
    			break;
    			
    		case "+end" :
    			if (isEndParsed == false && (i + 2) < params.size()) {
    				log.log(Level.INFO, "+end detected\n");
    				isEndParsed = true;
    				String date = params.get(i + 1);
    				String time = params.get(i + 2);
    				newEnd = parseDateTime(date + " " + time);
    				endAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 3; // skip past 2 words, which is the new date and time 
    			} else {
    				log.log(Level.INFO, "aborting, " + String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg) + "\n");
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg));
    			}
    			break;
  			
    		case "-end" :
    			if (isEndParsed == false) {
    				log.log(Level.INFO, "-end detected\n");
    				endAction = DeltaTask.FIELD_ACTION.REMOVE;
    				isEndParsed = true;
    				i++;
    			} else {
    				log.log(Level.INFO, "aborting, " + String.format(ERROR_INVALID_FIELD_TO_REMOVE, "end date") + "\n");
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_REMOVE, "end date"));
    			}
    			break;
    			
    		case "+start" :
    			if (isStartParsed == false && (i + 2) < params.size()) {
    				log.log(Level.INFO, "+start detected\n");
    				isStartParsed = true;
    				String date = params.get(i + 1);
    				String time = params.get(i + 2);
    				newStart = parseDateTime(date + " " + time);
    				startAction = DeltaTask.FIELD_ACTION.UPDATE;
    				i += 3; // skip past 2 words, which is the new date and time
    			} else {
    				log.log(Level.INFO, "aborting, " + String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg) + "\n");
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_UPDATE, "date and time", arg));
    			}
    			break;
  			
    		case "-start" :
    			if (isStartParsed == false) {
    				log.log(Level.INFO, "+start detected\n");
    				startAction = DeltaTask.FIELD_ACTION.REMOVE;
    				isStartParsed = true;
    				i++;
    			} else {
    				log.log(Level.INFO, "aborting, " + String.format(ERROR_INVALID_FIELD_TO_REMOVE, "start date") + "\n");
    				throw new Exception(String.format(ERROR_INVALID_FIELD_TO_REMOVE, "start date"));
    			}
    			break;
    		
  			default :
  				log.log(Level.INFO, "aborting, " + arg + " is an unrecognized keyword\n");
  				throw new Exception(String.format(ERROR_UNRECOGNIZED_UPDATE_TOKEN, arg));
    	}
  	}
  	
  	return new DeltaTask(nameAction, newName, startAction, newStart, endAction, newEnd);
  }
    
  private static int countQuotesInString(String input) {
  	int quoteCount = 0;
  	for (int i = 0; i < input.length(); i++) {
  		if (input.charAt(i) == '"') {
  			quoteCount++;
  		}
  	}
  	
  	return quoteCount;
  }
  
  /*
   *Verifies if a task name is valid
   *A valid name must have at least 1 non-whitespace character, and must be surrounded by quotes
   */
  private static void verifyTaskNameValidity(String name) throws Exception {
	  char first = name.charAt(0), last = name.charAt(name.length()-1);
	  
if (name.length() <= 2 || first != '"' || last != '"') {
	log.log(Level.INFO, "aborting, " + name + " is invalid as it is not in quotes\n");
throw new Exception(ERROR_NAME_SHOULD_BE_IN_QUOTES );	
}

String nameWithQuotesRemoved = name.substring(1, name.length()-1);
if (nameWithQuotesRemoved.trim().length() == 0) {
	log.log(Level.INFO, "aborting, " + name + " is invalid as it has no non-whitespace chars\n");
	throw new Exception(ERROR_NAME_SHOULD_CONTAIN_NON_WHITESPACE_CHARS);
}
  }
  
}

