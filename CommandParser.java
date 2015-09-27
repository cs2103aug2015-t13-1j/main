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
    private static final int POSITION_COMMAND_TYPE = 0;
    private static final int POSITION_FIRST_PARAM = 1;
    // the regex pattern to split input by spaces, except if there is a quoted string
private static final Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
// the maximum number of args for command types that take in arguments
private static final int MAX_ARG_ADD = 1;
private static final int MAX_ARG_REMOVE = 1;
 
    public static Command getCommandFromInput(String input) {
    	ArrayList<String> params = splitInput(input);
    	String commandType = getCommandType(params).toLowerCase();
    	ArrayList<String> args = getCommandArgs(params);
    	
    	switch(commandType) {
    	case "add":
    		return initAddCommand(args);
    	case "list":
    		return initListCommand();
    	case "remove":
    		return initRemoveCommand(args);
    	case "exit":
    	case "quit":
    		return initExitCommand();
    	default:
    		return initInvalidCommand();
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

    private static Command initInvalidCommand() {
    	return new Command(Command.Type.INVALID);
    }
    
    private static Command initListCommand() {
    	return new Command(Command.Type.LIST);
    }
    
    private static Command initExitCommand() {
    	return new Command(Command.Type.EXIT);
    }
    
    private static Command initAddCommand(ArrayList<String> args) {
if (args.size() == 0 || args.size() > MAX_ARG_ADD) {    	
    	return initInvalidCommand();
    }

Task newTask = new Task(args.get(0));
return new Command(Command.Type.ADD, newTask);
    }
    
    private static Command initRemoveCommand(ArrayList<String> args) {
if (args.size() == 0 || args.size() > MAX_ARG_REMOVE) {    	
    	return initInvalidCommand();
    }

Task taskToRemove = new Task(args.get(0));
return new Command(Command.Type.REMOVE, taskToRemove);
    }
    
}
