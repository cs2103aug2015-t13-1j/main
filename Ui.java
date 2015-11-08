//@@author A0145732H
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * The UI class handles all user input and output to the screen.
 * It sends the user input to the logic component for processing,
 * and it handles displaying the command feedback and errors to the user. 
 * Exceptions thrown by the logic component to indicate errors are handled by UI. 
 * 
 * @author Katherine Coronado
 *
 */

public class Ui {
	/** text marker to indicate whether a task is completed or not when displaying tasks **/
	public static final String MARKER_UNCOMPLETED = "     ";
	public static final String MARKER_DONE = "DONE ";
	
	/** Jansi tags for color coding strings based on the date **/
	private static final String COLOR_CODE_END_TAG = "|@";
	public static final String COLOR_CODE_FUTURE = "@|CYAN ";
	public static final String COLOR_CODE_TOMORROW = "@|GREEN ";
	public static final String COLOR_CODE_TODAY = "@|YELLOW ";
	public static final String COLOR_CODE_OVERDUE = "@|RED ";
	
	/** messages to be displayed to the user **/
	private static final String MESSAGE_WELCOME = "Welcome to HelloTask!\n\n";
	private static final String MESSAGE_COMMAND_PROMPT = "> ";
	
	/** messages and message formats for tasks in the task list display **/
	private static final String MESSAGE_NO_TASKS = "No tasks to display.";
	public static final String MESSAGE_UNSCHEDULED = "%s%d. %s\n";
	public static final String MESSAGE_DEADLINE = "%s%d. %s\n\tdue %s at %s\n";
	public static final String MESSAGE_EVENT = "%s%d. %s\n\t%s to %s\n";
	public static final String MESSAGE_UNSCHEDULED_HEADER = "\nUnscheduled tasks:\n";
	public static final String MESSAGE_DEADLINE_HEADER = "\nDeadlines:\n";
	public static final String MESSAGE_EVENTS_HEADER = "\nEvents:\n";
	public static final String MESSAGE_DATE = "%s, %d %s";
	public static final String MESSAGE_DATE_YEAR = MESSAGE_DATE + " %d";
	public static final String MESSAGE_TIME = "%d:%02d %s";
	public static final String MESSAGE_DATE_TIME = "%s at %s";
	
	/** message formats for user command feedback **/
	private static final String MESSAGE_FEEDBACK_EVENT = "\"%s\", scheduled from %s to %s";
	private static final String MESSAGE_FEEDBACK_DEADLINE = "\"%s\" due by %s";
	private static final String MESSAGE_FEEDBACK_UNSCHEDULED = "\"%s\"";
	
	private static boolean isRunning;
	private static Scanner keyboard;
	private static ArrayList<Task> currentTaskList;
	private static Logic logic;
	
	public static void main(String[] args) throws Exception {
		taskBuddyInit();
		displayWelcomeMessage();
		while (isRunning) {
			currentTaskList = logic.updateCurrentTaskList();
			showToUser(logic.getDefaultView());
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
	 * @throws Exception 
	 */
	private static void taskBuddyInit() throws Exception {
		AnsiConsole.systemInstall();
		keyboard = new Scanner(System.in);
		isRunning = true;
		logic = new Logic();
		logic.init(new StorageManager(), new Logic());
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
	 * @throws Exception 
	 */
	private static void taskBuddyClose() throws Exception {
		logic.close();
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
			Command command = logic.processUserInput(userInput);
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
			boolean isFirstEvent = true;
			boolean isFirstDeadline = true;
			boolean isFirstUnscheduled = true;
			
			for (Task task : taskList) {
				LocalDateTime start = task.getStartDateTime();
				LocalDateTime end = task.getEndDateTime();
				
				if (end == null && start == null) {
					isFirstUnscheduled = writeUnscheduledToList(message, taskNumber++, task, isFirstUnscheduled);
				} else if (start == null) {
					isFirstDeadline = writeDeadlineToList(message, taskNumber++, task, isFirstDeadline);
				} else {
					isFirstEvent = writeEventToList(message, taskNumber++, task, isFirstEvent);
				}
			}
			return message.toString();
		} else {
			return MESSAGE_NO_TASKS;
		}
	}

	/**
	 * Write an event task in the proper formatting to the task list display message
	 * @param message		the StringBuilder holding the current task list display message
	 * @param taskNumber	the number associated with the task
	 * @param task			the task to format and display
	 * @param isFirst		whether this is the first event in the display so far
	 * @return				whether this is the first event in the display so far
	 */
	public static boolean writeEventToList(StringBuilder message, 
			int taskNumber, Task task, boolean isFirst) {
		String taskName = task.getName();
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		
		String doneMarker;
		if (task.isDone()) {
			doneMarker = MARKER_DONE;
		} else {
			doneMarker = MARKER_UNCOMPLETED;
		}
		
		if (isFirst) {
			message.append(MESSAGE_EVENTS_HEADER);
			isFirst = false;
		}
		if (start.toLocalDate().compareTo(end.toLocalDate()) == 0) {
			// same start and end date -> only show the end time
			message.append(String.format(MESSAGE_EVENT, doneMarker, taskNumber, taskName, 
					getDateTimeFormat(start), getTimeFormat(end)));
		} else {
			message.append(String.format(MESSAGE_EVENT, doneMarker, taskNumber, taskName, 
					getDateTimeFormat(start), getDateTimeFormat(end)));
		}
		return isFirst;
	}

	/**
	 * Write a deadline task in the proper formatting to the task list display message
	 * @param message		the StringBuilder holding the current task list display message
	 * @param taskNumber	the number associated with the task
	 * @param task			the task to format and display
	 * @param isFirst		whether this is the first deadline in the display so far
	 * @return				whether this is the first deadline in the display so far
	 */
	public static boolean writeDeadlineToList(StringBuilder message,
			int taskNumber, Task task, boolean isFirst) {
		String taskName = task.getName();
		LocalDateTime end = task.getEndDateTime();

		String doneMarker;
		if (task.isDone()) {
			doneMarker = MARKER_DONE;
		} else {
			doneMarker = MARKER_UNCOMPLETED;
		}
		
		if (isFirst) {
			message.append(MESSAGE_DEADLINE_HEADER);
			isFirst = false;
		}
		message.append(String.format(MESSAGE_DEADLINE, doneMarker, taskNumber, taskName,
				getDateFormat(end), getTimeFormat(end)));
		return isFirst;
	}

	/**
	 * Write an unscheduled task in the proper formatting to the task list display message
	 * @param message		the StringBuilder holding the current task list display message
	 * @param taskNumber	the number associated with the task
	 * @param task			the task to format and display
//	 * @param isFirst		whether this is the first unscheduled task in the display so far
	 * @return				whether this is the first unscheduled task in the display so far
	 */
	public static boolean writeUnscheduledToList(StringBuilder message, 
			int taskNumber, Task task, boolean isFirst) {
		String taskName = task.getName();

		String doneMarker;
		if (task.isDone()) {
			doneMarker = MARKER_DONE;
		} else {
			doneMarker = MARKER_UNCOMPLETED;
		}
		
		if (isFirst) {
			message.append(MESSAGE_UNSCHEDULED_HEADER);
			isFirst = false;
		}
		message.append(String.format(MESSAGE_UNSCHEDULED, doneMarker, taskNumber, taskName));
		return isFirst;
	}
	
	/**
	 * Add color coding to a string based on the given time.
	 * 
	 * @param message	the string to add color coding to
	 * @param dateTime		the date to determine the color coding
	 * @return			the jansi formatted color coded string
	 */
	public static String addColorCoding(String message, LocalDateTime dateTime) {
		if (dateTime == null) {
			return COLOR_CODE_FUTURE + message + COLOR_CODE_END_TAG;
		} else if (dateTime.compareTo(LocalDateTime.now()) < 0) {
			return COLOR_CODE_OVERDUE + message + COLOR_CODE_END_TAG;
		} else if (dateTime.toLocalDate().compareTo(LocalDate.now()) == 0) {
			return COLOR_CODE_TODAY + message + COLOR_CODE_END_TAG;
		} else if (dateTime.toLocalDate().compareTo(LocalDate.now().plusDays(1)) == 0) {
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
	public static String getDateFormat(LocalDateTime dateTime) {
		LocalDate date = dateTime.toLocalDate();
		String month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		String day = date.getDayOfWeek().toString();
		day = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
		LocalDate today = LocalDate.now();
		String message;
		
		// only display the year if it is different from the current year
		if (dateTime.getYear() != today.getYear()) {
			message = String.format(MESSAGE_DATE_YEAR, day, dateTime.getDayOfMonth(), month, dateTime.getYear());
		} else if (date.compareTo(today) == 0) {
			message = "today";
		} else if (date.compareTo(today.plusDays(1)) == 0) {
			message = "tomorrow";
		} else {
			message = String.format(MESSAGE_DATE, day, dateTime.getDayOfMonth(), month);
		}
//		return message;
		return addColorCoding(message, dateTime);
	}
	
	/**
	 * Format the time into h:mm AM/PM
	 * 
	 * @param dateTime	the LocalDateTime to format
	 * @return			the string in the proper time format
	 */
	public static String getTimeFormat(LocalDateTime dateTime) {
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
//		return message;
		return addColorCoding(message, dateTime);
	}
	
	/**
	 * This method creates a String in the format dd mmm hh:mm, i.e. 24 Oct 13:00
	 * 
	 * @param dateTime	The LocalDateTime with the date and time to format
	 * @return			a String in the format dd mmm hh:mm
	 */
	public static String getDateTimeFormat(LocalDateTime dateTime) {
		return String.format(MESSAGE_DATE_TIME, getDateFormat(dateTime), getTimeFormat(dateTime));
	}
	
	//@@author A0126270N
	/*
	 *Converts the information in Task to a String more suitable for printing.  
	 *This is not for UI's column task list display, but is for providing confirmation for commands like add and undo
	 */
	public static String getPrintableTaskString(Task task) {
		String taskName = task.getName();
		LocalDateTime start = task.getStartDateTime();
		LocalDateTime end = task.getEndDateTime();
		String message = null;;
		
		if (start != null && end != null) {
			message = String.format(MESSAGE_FEEDBACK_EVENT, taskName, getDateTimeFormat(start), getDateTimeFormat(end));
		} else if (start == null && end != null) {
			message = String.format(MESSAGE_FEEDBACK_DEADLINE, taskName, getDateTimeFormat(end));
		} else {
			message = String.format(MESSAGE_FEEDBACK_UNSCHEDULED, taskName);
		}
		return message;
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
	private static void showToUser(String message) {
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
