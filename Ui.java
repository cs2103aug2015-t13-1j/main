import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
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

public class Ui {
	/** messages to be displayed to the user **/
	private static final String MESSAGE_WELCOME = "Welcome to TaskBuddy!\n\n";
	private static final String MESSAGE_COMMAND_PROMPT = "> ";
	
	/** messages and message formats for tasks in the task list display **/
	private static final String MESSAGE_NO_TASKS = "No tasks to display.";
	private static final String MESSAGE_LIST_HEADER = " #  Start\t\t | End\t\t | Name\n";
	private static final String MESSAGE_FLOATING = "%2d. \t\t\t | \t\t | %s\n";
	private static final String MESSAGE_DEADLINE = "%2d. \t\t\t | %s\t | %s\n";
	private static final String MESSAGE_EVENT = "%2d. %s \t | %s\t | %s\n";
	private static final String MESSAGE_DATE_TIME_FORMAT = "%02d %s %d:%02d";
	
	private static boolean isRunning;
	private static Scanner keyboard;
	private static ArrayList<Task> currentTaskList;
	
	public static void main(String[] args) {
		taskBuddyInit();
		displayWelcomeMessage();
		while (isRunning) {
			currentTaskList = Logic.updateCurrentTaskList();
			showToUser(Logic.getDefaultView());
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
		Logic.init();
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
		Logic.close();
		keyboard.close();
		System.exit(0);
	}
	
	/**
	 * This method will handle the user's input to be evaluated and executed
	 * 
	 * @param userInput	the string entered by the user
	 */
	public static void executeUserInput(String userInput) {
		try {
			Command command = Logic.processUserInput(userInput);
			showToUser(command.getSuccessMessage() + "\n\n");
		} catch (Exception e) {
			showToUser(e.getMessage() + "\n\n");
		}
	}
	
	/**
	 * This method formats the tasks in the task list into a user-friendly display
	 * 
	 * @param taskList	the task list to format
	 * @return			a String containing the formatted task list display
	 */
	public static String createTaskListDisplay(ArrayList<Task> taskList) {
		if (taskList.size() > 0) {
			StringBuilder message = new StringBuilder();
			message.append(MESSAGE_LIST_HEADER);
			int taskNumber = 1;
			for (Task task : taskList) {
				LocalDateTime start = task.getStartDateTime();
				LocalDateTime end = task.getEndDateTime();
				String taskName;
				if (task.isDone()) {
					taskName = "*" + task.getName();
				} else {
					taskName = task.getName();
				}
				if (end == null && start == null) {
					message.append(String.format(MESSAGE_FLOATING, taskNumber++, taskName));
				} else if (start == null) {
					message.append(String.format(MESSAGE_DEADLINE, taskNumber++, 
							getDateTimeFormat(end), taskName));
				} else {
					message.append(String.format(MESSAGE_EVENT, taskNumber++, getDateTimeFormat(start), 
							getDateTimeFormat(end), taskName));
				}
			}
			message.append("\n* = completed tasks");
			return message.toString();
		} else {
			return MESSAGE_NO_TASKS;
		}
	}
	
	/**
	 * This method creates a String in the format dd mmm hh:mm, i.e. 24 Oct 13:00
	 * 
	 * @param dateTime	The LocalDateTime with the date and time to format
	 * @return			a String in the format dd mmm hh:mm
	 */
	private static String getDateTimeFormat(LocalDateTime dateTime) {
		String month = dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		return String.format(MESSAGE_DATE_TIME_FORMAT, dateTime.getDayOfMonth(), 
				month, dateTime.getHour(), dateTime.getMinute());
	}
	
	/**
	 * Returns the task list that was most recently displayed to the user. 
	 * 
	 * @return 	an ArrayList of tasks that were most recently displayed to the user,
	 * 			or null if the most recent command was not a List command
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
