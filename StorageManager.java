import java.io.File;
import java.io.IOException;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	private static final String DIRECTORY = "./";
	private static final String FILE_NAME = "TaskStorage";
	private static final String FILE_TYPE = ".json";
	private static FileReader fileReader;
	private static FileWriter fileWriter;
	private static BufferedReader bufferedReader;
	private static BufferedWriter bufferedWriter;
	private static Task[] TASK_LIST = {};
	private static final Task[] EMPTY_TASK = {};

	public StorageManager() {
	}

	public static void openStorage() {
		try {
			File file = new File(DIRECTORY + FILE_NAME + FILE_TYPE);

			if (!file.exists()) {
				file.createNewFile();
			}

			fileReader = new FileReader(file.getAbsoluteFile());
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedReader = new BufferedReader(fileReader);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			TASK_LIST = initiateTaskList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Task[] initiateTaskList() {
		try {
			Gson gson = new Gson();
			Task[] taskListFromJSON;
			
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			
			if (taskListFromJSON == null) {
				taskListFromJSON = EMPTY_TASK;
			}
			
			return taskListFromJSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void closeStorage() {
		try {
			bufferedReader.close();
			bufferedWriter.close();
			fileReader.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method searches the current task list for all tasks that match the name
	 * @param name	the name of the task to search for
	 * @return		an array of tasks containing the Tasks that match the name
	 */
	public static Task[] readTask(String name) {
		Task[] taskListToReturn = new Task[TASK_LIST.length];
		
		int j = 0;
		
		for (int i = 0; i < TASK_LIST.length; i++) {
			if (TASK_LIST[i].getName().equals(name)) {
				taskListToReturn[j] = TASK_LIST[i];
				j++;
			}
		}
		
		return taskListToReturn;
	}
	
	/**
	 * This method searches the task list for tasks containing all of the given keywords
	 * 
	 * @param keywords	the array of keywords to search for in the task names
	 * @return			an ArrayList of the tasks containing all of the keywords
	 * 					The ArrayList will be empty if no tasks were found.
	 */
	public static ArrayList<Task> searchTasks(String[] keywords) {
		ArrayList<Task> foundTasks = new ArrayList<Task>();
		
		for (int i = 0; i < TASK_LIST.length; i++) {
			int keywordIndex = 0;
			Task currentTask = TASK_LIST[i];
			
			// check if currentTask contains all of the keywords before adding to foundTasks
			while (keywordIndex < keywords.length) {
				if (!currentTask.getName().contains(keywords[keywordIndex++])) {
					break;
				}
				if (keywordIndex == keywords.length) {
					foundTasks.add(currentTask);
				}
			}
		}
		
		return foundTasks;
	}
			
//	public static Task[] readTask(String name) {
//		try {
//			Gson gson = new Gson();
//			Task[] taskListFromJSON;
//			Task[] taskListToReturn;
//			
//			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
//			taskListToReturn = new Task[taskListFromJSON.length];
//			
//			if (taskListFromJSON != null) { 
//				int j = 0;
//				
//				for (int i = 0; i < taskListFromJSON.length; i++) {
//					if (taskListFromJSON[i].getName().equals(name)) {
//						taskListToReturn[j] = taskListFromJSON[i];
//						j++;
//					}
//				}
//			}
//
//			return taskListToReturn;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public static Task[] readAllTasks() {
		return TASK_LIST;
	}

	public static void writeTask(Task task) {
		try {
			Gson gson = new Gson();
			ArrayList<Task> taskListTransition;
			Task[] taskListToReturn;
			
//			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
//			
//			if (taskListFromJSON == null) {
//				taskListFromJSON = EMPTY_TASK;
//			}
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(TASK_LIST));
			
			taskListTransition.add(task);
			
			taskListToReturn = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			TASK_LIST = new Task[taskListToReturn.length];
			TASK_LIST = taskListToReturn;
			
			gson.toJson(taskListToReturn, bufferedWriter);
			bufferedWriter.flush();
			// bufferedWriter.write(gson.toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method removes a given task from the file
	 * 
	 * @param task
	 * @throws Exception	if the task was unable to be removed
	 */
	public static void removeTask(Task task) throws Exception {
		boolean isRemoved = false;
		try {
			Gson gson = new Gson();
			ArrayList<Task> taskListTransition;
			Task[] taskListToUpdate;
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(TASK_LIST));
			
			isRemoved = taskListTransition.remove(task);
			
			taskListToUpdate = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			TASK_LIST = new Task[taskListToUpdate.length];
			TASK_LIST = taskListToUpdate;
			
			gson.toJson(taskListToUpdate, bufferedWriter);
			bufferedWriter.flush();
			
		} catch (Exception e) {
//			e.printStackTrace();
			// TODO is this what would throw the exception?
			// if so we should re-add the removed task to the local copy to reflect the file
			isRemoved = false;
			throw new Exception("Error saving changes to file.");
		}
		
		if (!isRemoved) {
			throw new Exception("\"" + task.getName() + "\" was not found.");
		}
	}

	/**
	 * This method updates a task in the task list with the new task.
	 * 
	 * @param oldTask		the task to search for and update
	 * @param newTask		the updated version of the task to replace the old task
	 * @throws Exception	if there are no tasks or if the old task was not found
	 */
	public static void updateTask(Task oldTask, Task newTask) throws Exception {
		if (TASK_LIST.length == 0) {
			throw new Exception("You currently do not have any tasks saved.");
		}
		
		Task taskToUpdate = TASK_LIST[0];
		int index = 0;
		while (index < TASK_LIST.length) {
			taskToUpdate = TASK_LIST[index];
			if (!taskToUpdate.equals(oldTask)) {
				index++;
			} else {
				break;
			}
		}
		
		if (index == TASK_LIST.length) {
			throw new Exception("Task \"" + oldTask.getName() + "\" was not found.");
		} else {	
			TASK_LIST[index] = newTask;
		}
	}
}