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
private static Pattern splitter = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static Command getCommandFromInput(String input) {
    	ArrayList<String> params = splitInput(input);
    	String commandType = getCommandType(params).toLowerCase();
    	ArrayList<String> args = getCommandArgs(params);
    	
    	switch(commandType) {
    	case "list":
    		return initListCommand();
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
}
