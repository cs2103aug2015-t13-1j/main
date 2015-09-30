import java.util.Scanner;

public class UI {
	private static boolean isRunning;
	
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		isRunning = true;
		
		displayWelcomeMessage();
		while (isRunning) {
			String userInput = keyboard.nextLine();
			executeUserInput(userInput);
		}
		keyboard.close();
	}

	/**
	 * This method displays a welcome message and other relevant tasks to the user
	 * upon opening the program.
	 */
	private static void displayWelcomeMessage() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method will handle the user's input to be evaluated and executed
	 * 
	 * @param userInput	the string entered by the user
	 */
	private static void executeUserInput(String userInput) {
		// TODO Auto-generated method stub
		try {
//			Logic.processUserInput(userInput);
		} catch (Exception e) {
			
		}
	}

	/**
	 * This method will display the task list to the user
	 * 
	 * @param tasks	the array holding the list of tasks to display to the user
	 */
	public static void listTasks(Task[] tasks) {
		
	}
	
	/**
	 * This is a generic method to display a success message to the user after executing a command
	 * 
	 * @param command	the command that the user requested to execute
	 */
	public static void displayCommandSuccess(Command command) {
		switch (command.getCommandType()) {
			case ADD :
				displayAddSuccess(command.getCommandTask());
				break;
				
			case UPDATE : 
				displayUpdateSuccess(command.getCommandTask());
				break;
				
			case REMOVE :
				displayRemoveSuccess(command.getCommandTask());
				break;
				
			case EXIT :
				exitProgram();
				break;
				
			// TODO handle case for INVALID and LIST
				
			default :
				break;
		}
	}

	/**
	 * This method displays a success method upon successful completion of an add command
	 * 
	 * @param task	the task that the user added
	 */
	private static void displayAddSuccess(Task task) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method displays a success method upon successful completion of an update command
	 * 
	 * @param task	the updated task that was edited by the user
	 */
	private static void displayUpdateSuccess(Task task) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method displays a success method upon successful completion of a remove command
	 * 
	 * @param task	the task that was removed by the user
	 */
	private static void displayRemoveSuccess(Task task) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method displays an exit message and closes out of the program
	 */
	private static void exitProgram() {
		// TODO Auto-generated method stub
		isRunning = false;
	}
}