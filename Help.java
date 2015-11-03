
public class Help extends Command {
	private String helpMessage;
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		setHelpMessage();
	}
	
	private void setHelpMessage() {
		helpMessage = "HELPPP";
	}

	@Override
	public String getSuccessMessage() {
		// TODO Auto-generated method stub
		return helpMessage;
	}
}
