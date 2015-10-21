import java.util.ArrayList;
import java.util.Scanner;

/**
 * The UI class handles all user input and output to the screen.
 * It sends the user input to the Logic component for processing,
 * and it handles displaying the command feedback and errors to the user. 
 * Exceptions thrown by the Logic component to indicate errors are handled by UI. 
 * 
 * @author Katherine Coronado
 *;
 */

public class Ui {
	/** messages to be displayed to the user **/
	private static final String MESSAGE_WELCOME = "Welcome to TaskBuddy!\n\n";
	private static final String MESSAGE_COMMAND_PROMPT = "> ";
	
	private static boolean isRunning;
	private static Scanner keyboard;
	private static ArrayList<Task> currentTaskList;
	
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
			Command command = Logic.processUserInput(userInput);
			showToUser(command.getSuccessMessage() + "\n\n");
			// update the current saved task list so that tasks can be referred to by index
			if (command.getClass() == List.class) {
				List listCommand = (List) command;
				currentTaskList = listCommand.getTaskList();
			} else {
				currentTaskList = null;
			}
		} catch (Exception e) {
			showToUser(e.getMessage() + "\n\n");
		}
	}
	
	/**
	 * Returns the task list that was most recently displayed to the user. 
	 * 
	 * @return 	an ArrayList of tasks that were most recently displayed to the user
	 * 			null if the most recent command was not a List command
	 */
	public static ArrayList<Task> getCurrentTaskList() {
		return currentTaskList;
	}
	
	/**
	 * This method shows a message to the user in the console
	 * 
	 * @param message	the message to display to the user
	 */
	public static void showToUser(String message) {
		System.out.print(message);
	}
	
	/**
	 * This method flags that the user requested to exit the program
	 */
	public static void indicateExit() {
		isRunning = false;
	}
}
