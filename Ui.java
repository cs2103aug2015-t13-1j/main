//@@author A0145732H
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
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
	/** Jansi tags for color coding strings based on the date **/
	private static final String COLOR_CODE_END_TAG = "|@";
	private static final String COLOR_CODE_COMPLETED = "@|BLUE ";
	private static final String COLOR_CODE_FUTURE = "@|CYAN ";
	private static final String COLOR_CODE_TOMORROW = "@|GREEN ";
	private static final String COLOR_CODE_TODAY = "@|YELLOW ";
	private static final String COLOR_CODE_OVERDUE = "@|RED ";
	
	/** messages to be displayed to the user **/
	private static final String MESSAGE_WELCOME = "Welcome to TaskBuddy!\n\n";
	private static final String MESSAGE_COMMAND_PROMPT = "> ";
	
	/** messages and message formats for tasks in the task list display **/
	private static final String MESSAGE_NO_TASKS = "No tasks to display.";
	private static final String MESSAGE_FLOATING = "%d. %s\n";
	private static final String MESSAGE_DEADLINE = "%d. %s\n\tdue %s at %s\n";
	private static final String MESSAGE_EVENT = "%d. %s\n\t%s to %s\n";
	private static final String MESSAGE_DATE = "%s, %d %s";
	private static final String MESSAGE_DATE_YEAR = MESSAGE_DATE + " %d";
	private static final String MESSAGE_TIME = "%d:%02d %s";
	
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
		AnsiConsole.systemInstall();
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
		AnsiConsole.systemUninstall();
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
			int taskNumber = 1;
			for (Task task : taskList) {
				LocalDateTime start = task.getStartDateTime();
				LocalDateTime end = task.getEndDateTime();
				String taskName = task.getName();
				if (task.isDone()) {
					// color the whole task line as completed
					message.append(COLOR_CODE_COMPLETED);
				} else {
					// color just the task name based on the date
					if (start == null || start.compareTo(LocalDateTime.now()) < 0) {
						taskName = addColorCoding(task.getName(), end);
					} else {
						taskName = addColorCoding(task.getName(), start);
					}
				}
				
				if (end == null && start == null) {
					message.append(String.format(MESSAGE_FLOATING, taskNumber++, taskName));
				} else if (start == null) {
					message.append(String.format(MESSAGE_DEADLINE, taskNumber++, taskName,
							getDateFormat(end), getTimeFormat(end)));
				} else {
					if (Logic.compareDates(start, end) == 0) {
						// same start and end date -> only show the end time
						message.append(String.format(MESSAGE_EVENT, taskNumber++, taskName, 
								getDateTimeFormat(start), getTimeFormat(end)));
					} else {
						message.append(String.format(MESSAGE_EVENT, taskNumber++, taskName, 
								getDateTimeFormat(start), getDateTimeFormat(end)));
					}
				}
				if (task.isDone()) {
					message.append(COLOR_CODE_END_TAG);
				}
			}
			return message.toString();
		} else {
			return MESSAGE_NO_TASKS;
		}
	}
	
	/**
	 * Add color coding to a string based on the given time.
	 * 
	 * @param message	the string to add color coding to
	 * @param dateTime	the date to determine the color coding
	 * @return			the jansi formatted color coded string
	 */
	private static String addColorCoding(String message, LocalDateTime dateTime) {
		if (dateTime == null) {
			return COLOR_CODE_FUTURE + message + COLOR_CODE_END_TAG;
		} else if (dateTime.compareTo(LocalDateTime.now()) < 0) {
			return COLOR_CODE_OVERDUE + message + COLOR_CODE_END_TAG;
		} else if (Logic.compareDates(dateTime, LocalDateTime.now()) == 0) {
			return COLOR_CODE_TODAY + message + COLOR_CODE_END_TAG;
		} else if (Logic.compareDates(dateTime, Logic.getTomorrowsDate()) == 0) {
			return COLOR_CODE_TOMORROW + message + COLOR_CODE_END_TAG;
		} else {
			return COLOR_CODE_FUTURE + message + COLOR_CODE_END_TAG;
		}
	}
	
	/**
	 * Format the date into dd mmm or dd mmm yyyy depending on whether the year is the same as this year.
	 * 
	 * @param dateTime	the LocalDateTime to format
	 * @return			the string in the proper date format
	 */
	private static String getDateFormat(LocalDateTime dateTime) {
		String month = dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		String day = dateTime.getDayOfWeek().toString();
		day = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
		LocalDateTime today = LocalDateTime.now();
		String message;
		
		// only display the year if it is different from the current year
		if (dateTime.getYear() != today.getYear()) {
			message = String.format(MESSAGE_DATE_YEAR, day, dateTime.getDayOfMonth(), month, dateTime.getYear());
		} else if (Logic.compareDates(dateTime, LocalDateTime.now()) == 0) {
			message = "today";
		} else if (Logic.compareDates(dateTime, Logic.getTomorrowsDate()) == 0) {
			message = "tomorrow";
		} else {
			message = String.format(MESSAGE_DATE, day, dateTime.getDayOfMonth(), month);
		}
		return message;
//		return addColorCoding(message, dateTime);
	}
	
	/**
	 * Format the time into h:mm AM/PM
	 * 
	 * @param dateTime	the LocalDateTime to format
	 * @return			the string in the proper time format
	 */
	private static String getTimeFormat(LocalDateTime dateTime) {
		int hour = dateTime.getHour() % 12;
		if (hour == 0) {
			hour += 12;
		}
		String message;
		if (dateTime.getHour() < 12) {
			message = String.format(MESSAGE_TIME, hour, dateTime.getMinute(), "AM");
		} else {
			message = String.format(MESSAGE_TIME, hour, dateTime.getMinute(), "PM");
		}
		return message;
//		return addColorCoding(message, dateTime);
	}
	
	/**
	 * This method creates a String in the format dd mmm hh:mm, i.e. 24 Oct 13:00
	 * 
	 * @param dateTime	The LocalDateTime with the date and time to format
	 * @return			a String in the format dd mmm hh:mm
	 */
	private static String getDateTimeFormat(LocalDateTime dateTime) {
		return getDateFormat(dateTime) + " at " + getTimeFormat(dateTime);
	}
	
	//@@author A0126270N
	/*
	 *Converts the information in Task to a String more suitable for printing.  
	 *This is not for UI's column task list display, but is for providing confirmation for commands like add and undo
	 */
	public static String getPrintableTaskString(Task task) {
		String str = "\"" + task.getName() + "\"";
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		
		if (start != null && end != null) {
			str += ", scheduled from " + getDateTimeFormat(start) + " to " + getDateTimeFormat(end);
		} else if (start == null && end != null) {
			str += ", due by " + getDateTimeFormat(end);
		}
		
		return str;
	}

	//@@author A0145732H
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
		System.out.print(ansi().render(message));
		System.out.flush();
	}
	
	/**
	 * This method flags that the user requested to exit the program
	 */
	public static void indicateExit() {
		isRunning = false;
	}
}
