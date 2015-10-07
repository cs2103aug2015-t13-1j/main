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
	
	public static Task[] readAllTask() {
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
}