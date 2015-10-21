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
	private static final String ERROR_INCORRECT_ARG_UPDATE = "Please indicate the task you want to change "
																+ "and which values to change.";
	
	// positions in the command input
	private static final int POSITION_COMMAND_TYPE = 0;
    private static final int POSITION_FIRST_PARAM = 1;
    
    // the regex pattern to split input by spaces, except if there is a quoted string
	private static final Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	
	// the maximum number of args for command types that take in arguments
	private static final int MAX_ARG_ADD = 1;
	private static final int MAX_ARG_REMOVE = 1;
	
	// positions in the parameter list for the update command
	private static final int POSITION_UPDATE_NEW = 1;
	private static final int POSITION_UPDATE_OLD = 0;
 
	/**
	 * Parse the input into the appropriate command
	 * 
	 * @param input			the input to parse
	 * @return				the command parsed from the input
	 * @throws Exception	if there are an incorrect number of arguments for a given command
	 * 						or if the command is unrecognized
	 */
    public static Command getCommandFromInput(String input) throws Exception {
    	ArrayList<String> params = splitInput(input);
    	String commandType = getCommandType(params).toLowerCase();
    	ArrayList<String> args = getCommandArgs(params);
    	
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
		if (args.size() == 0 || args.size() > MAX_ARG_ADD) {    	
	    	throw new Exception(String.format(ERROR_INCORRECT_ARG_SINGLE, "add"));
	    }
	
		Task newTask = new Task(args.get(0));
		return new Add(newTask);
    }
    
    private static Command initRemoveCommand(ArrayList<String> args) throws Exception {
    	if (args.size() == 0 || args.size() > MAX_ARG_REMOVE) {
	    	throw new Exception(String.format(ERROR_INCORRECT_ARG_SINGLE, "remove"));    		
    	}

		Task taskToRemove = new Task(args.get(0));
		return new Remove(taskToRemove);
    }


    private static Command initUpdateCommand(ArrayList<String> args) throws Exception {
    	if (args.size() != 2) {    	
	    	throw new Exception(ERROR_INCORRECT_ARG_UPDATE);
	    }

    	Task oldTask = new Task(args.get(POSITION_UPDATE_OLD));
    	Task newTask = new Task(args.get(POSITION_UPDATE_NEW));
    	return new Update(oldTask, newTask);
    }
}

