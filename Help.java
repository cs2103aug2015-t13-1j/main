//@@author A0100081E
public class Help extends Command {
	private String helpType;
	private String helpMessage;
	
	// list of help type that are supported
	private final String HELP_TYPE_ADD = "add";
	private final String HELP_TYPE_LIST = "list";
	private final String HELP_TYPE_REMOVE = "remove";
	private final String HELP_TYPE_UPDATE = "update";
	private final String HELP_TYPE_DONE = "done";
	private final String HELP_TYPE_UNDO = "undo";
	private final String HELP_TYPE_RELOCATE = "relocate";
	private final String HELP_TYPE_REFORMAT = "reformat";
	
	// color coding for JANSI
	private static final String COLOR_CODE_END_TAG = "|@";
	public static final String COLOR_CODE_CYAN = "@|CYAN ";
	public static final String COLOR_CODE_GREEN = "@|GREEN ";
	public static final String COLOR_CODE_YELLOW = "@|YELLOW ";
	public static final String COLOR_CODE_RED = "@|RED ";
	
	// list of help message displayed
	private final String HELP_ADD = 
			COLOR_CODE_CYAN + "Adding Task:\n" + COLOR_CODE_END_TAG
		+ "- Unscheduled Task: add \"<task name>\"\n"
		+ "- Deadline Task: add \"<task name>\" by <date> <time>\n"
		+ "- Event Task: add “<task name>” from <start date> <start time> to <end date> <end time>\n"
		+ "Note:\n"
		+ "<task name> should be wrapped in quotation\n"
		+ "<date> can be either DD-MM-YYYY or DD-MM (current year is assumed)\n"
		+ "<time> should be HH:MM in 24-hour notation";
	
	private final String HELP_LIST = 
			COLOR_CODE_CYAN + "List Task:\n" + COLOR_CODE_END_TAG
		+ "- General List: list\n"
		+ "- Unscheduled Tasks: list unscheduled\n"
		+ "- Deadline Tasks: list deadlines\n"
		+ "- Completed Tasks: list done\n"
		+ "- Event Tasks: list events\n"
		+ "- Today's Tasks: list today\n"
		+ "- Tomorrow's Tasks: list tomorrow\n"
		+ "- All Tasks: list all\n"
		+ "- Tasks with Keyword: list \"<first word> [additional words]\"\n"
		+ "Note:\n"
		+ "General list shows upto 15 tasks\n"
		+ "List can be calle with shortform, l\n"
		+ "List done has sysnosysms: completed and finished\n"
		+ "When listing with keywords, make sure words are separated with spaces";
	
	private final String HELP_REMOVE = 
			COLOR_CODE_CYAN + "Removing Task:\n" + COLOR_CODE_END_TAG
		+ "- remove <task number>\n"
		+ "Note:\n"
		+ "<task number> is the number shown from the most recent list command";
	
	private final String HELP_UPDATE = 
			COLOR_CODE_CYAN + "Updating Task:\n" + COLOR_CODE_END_TAG
		+ "- update <task number>";
	
	private final String HELP_DONE =
			COLOR_CODE_CYAN + "Marking Task Done:\n" + COLOR_CODE_END_TAG
		+ "- done <task number>\n"
		+ "Note:\n"
		+ "<task number> is the number shown from the most recent list command";
	
	private final String HELP_UNDO = 
			COLOR_CODE_CYAN + "Reverting Last Command:\n" + COLOR_CODE_END_TAG
		+ "- undo\n"
		+ "Note:\n"
		+ "Undo is limited to following commands: add, remove, update, done, reformat";
	
	private final String HELP_RELOCATE =
			COLOR_CODE_CYAN + "Relocating Storage File:\n" + COLOR_CODE_END_TAG
		+ "- relocate \"<absolute or relative folder path>\"\n"
		+ "Note:\n"
		+ "The folder path is case-sensitive";
	
	private final String HELP_REFORMAT = 
			COLOR_CODE_CYAN + "Clearing Content in Storage File:\n" + COLOR_CODE_END_TAG
		+ "- reformat"
		+ "Note:\n"
		+ "All saved content will be erased, please use with caution";
	
	private final String HELP_SUMMARY = 
			COLOR_CODE_YELLOW + "You can view specific help by specifying your query:\n" + COLOR_CODE_END_TAG
		+ "- Adding Task: help add\n"
		+ "- Listing Task: help list\n"
		+ "- Removing Task: help remove\n"
		+ "- Updating Task: help update\n"
		+ "- Marking Task as Complete: help done\n"
		+ "- Reverting Last Command: help undo\n"
		+ "- Relocating Storage File: help relocate\n"
		+ "- Cleearing Content in Stroage File: help reformat";
			
	public Help() {
		this.helpType = null;
	}
	
	public Help(String helpType) {
		this.helpType = helpType;
	}

	@Override
	public void execute() throws Exception {
		setHelpMessage();
	}
	
	private void setHelpMessage() {
		helpMessage = "";
		
		if (helpType == null) {
			helpMessage += HELP_ADD + "\n" + HELP_LIST + "\n" + HELP_REMOVE + "\n" + HELP_UPDATE + "\n" + HELP_DONE + "\n" + HELP_UNDO + "\n" + HELP_RELOCATE + "\n" + HELP_REFORMAT + "\n" + HELP_SUMMARY;
			
		} else if (helpType == HELP_TYPE_ADD) {
			helpMessage += HELP_ADD;
			
		} else if (helpType == HELP_TYPE_LIST) {
			helpMessage += HELP_LIST; 
			
		} else if (helpType == HELP_TYPE_REMOVE) {
			helpMessage += HELP_REMOVE;
			
		} else if (helpType == HELP_TYPE_UPDATE) {
			helpMessage += HELP_UPDATE;
			
		} else if (helpType == HELP_TYPE_DONE) {
			helpMessage += HELP_DONE;
			
		} else if (helpType == HELP_TYPE_UNDO) {
			helpMessage += HELP_UNDO;
			
		} else if (helpType == HELP_TYPE_RELOCATE) {
			helpMessage += HELP_RELOCATE;
			
		} else if (helpType == HELP_TYPE_REFORMAT) {
			helpMessage += HELP_REFORMAT;
			
		} 
	}

	@Override
	public String getSuccessMessage() {
		return helpMessage;
	}
}
