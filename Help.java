
public class Help extends Command {
	private String helpType;
	private String helpMessage;
	
	private final String HELP_ADD = "add";
	private final String HELP_REMOVE = "remove";
	private final String HELP_LIST = "list";
	
	public Help() {
		this.helpType = null;
	}
	
	public Help(String helpType) {
		this.helpType = helpType;
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		setHelpMessage();
	}
	
	private void setHelpMessage() {
		if (helpType == null) {
			helpMessage = "HELPPP";
		} else if (helpType == HELP_ADD) {
			helpMessage = "HELPPP ADDD";
		} else if (helpType == HELP_REMOVE) {
			helpMessage = "HELPPP REMOVE"; 
		} else if (helpType == HELP_LIST) {
			helpMessage = "HELPPP LISTTT";
		}
	}

	@Override
	public String getSuccessMessage() {
		// TODO Auto-generated method stub
		return helpMessage;
	}
}
