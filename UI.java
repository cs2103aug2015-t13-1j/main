import java.util.Scanner;

/**
 * The UI class handles all user input and output to the screen.
 * It sends the user input to the Logic component for processing,
 * and it handles displaying the command feedback and errors to the user. 
 * Exceptions thrown by the Logic component to indicate errors are handled by UI. 
 * 
 * @author Katherine Coronado
 *
 */

public class UI {
	/** messages to be displayed to the user **/
	private static final String MESSAGE_WELCOME = "Welcome to TaskBuddy!\n\n";
	private static final String MESSAGE_COMMAND_PROMPT = "> ";
	private static final String MESSAGE_SUCCESS_ADD = "\"%1$s\" added successfully\n\n";
	private static final String MESSAGE_SUCCESS_UPDATE = "\"%1$s\" updated successfully\n\n";
	private static final String MESSAGE_SUCCESS_REMOVE = "\"%1$s\" removed successfully\n\n";
	
	private static boolean isRunning;
	private static Scanner keyboard;
	
	public static void main(String[] args) {
		taskBuddyInit();
		displayWelcomeMessage();
		while (isRunning) {
			showToUser(MESSAGE_COMMAND_PROMPT);
			String userInput = getUserInput();
			executeUserInput(userInput);
		}
		taskBuddyClose();
	}

	/** 
	 * This method gets user input from the keyboard
	 * 
	 * @return	the user's input string
	 */
	private static String getUserInput() {
		return keyboard.nextLine();
	}

	/**
	 * This method initiates Task Buddy by initializing the class variables
	 */
	private static void taskBuddyInit() {
		keyboard = new Scanner(System.in);
		isRunning = true;
	}

	/**
	 * This method displays a welcome message and other relevant tasks to the user
	 * upon opening the program.
	 */
	private static void displayWelcomeMessage() {
		showToUser(MESSAGE_WELCOME);
		// TODO possibly show the list of upcoming tasks to the user
	}

	/**
	 * This method closes Task Buddy
	 */
	private static void taskBuddyClose() {
		keyboard.close();
		System.exit(0);
	}
	
	/**
	 * This method will handle the user's input to be evaluated and executed
	 * 
	 * @param userInput	the string entered by the user
	 */
	private static void executeUserInput(String userInput) {
		try {
			Logic.processUserInput(userInput);
		} catch (Exception e) {
			// TODO handle exception error messages
		}
	}

	/**
	 * This method will display the task list to the user
	 * 
	 * @param tasks	the array holding the list of tasks to display to the user
	 */
	public static void listTasks(Task[] tasks) {
		for (int i = 0; i < tasks.length; i++) {
			String entry = tasks[i].getName() + "\n";
			showToUser(entry);
		}
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
				indicateExit();
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
		String message = String.format(MESSAGE_SUCCESS_ADD, task.getName());
		showToUser(message);
	}

	/**
	 * This method displays a success method upon successful completion of an update command
	 * 
	 * @param task	the updated task that was edited by the user
	 */
	private static void displayUpdateSuccess(Task task) {
		String message = String.format(MESSAGE_SUCCESS_UPDATE, task.getName());
		showToUser(message);
		
	}
	
	/**
	 * This method displays a success method upon successful completion of a remove command
	 * 
	 * @param task	the task that was removed by the user
	 */
	private static void displayRemoveSuccess(Task task) {
		String message = String.format(MESSAGE_SUCCESS_REMOVE, task.getName());
		showToUser(message);
		
	}
	
	/**
	 * This method shows a message to the user in the console
	 * 
	 * @param message	the message to display to the user
	 */
	private static void showToUser(String message) {
		System.out.print(message);
	}
	
	/**
	 * This method flags that the user requested to exit the program
	 */
	private static void indicateExit() {
		isRunning = false;
	}
}