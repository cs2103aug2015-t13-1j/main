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
		} catch (IOException e) {
			e.printStackTrace();
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
		try {
			Gson gson = new Gson();
			Task[] taskListFromJSON;
			Task[] taskListToReturn;
			
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			taskListToReturn = new Task[taskListFromJSON.length];
			
			if (taskListFromJSON != null) { 
				int j = 0;
				
				for (int i = 0; i < taskListFromJSON.length; i++) {
					if (taskListFromJSON[i].getName().equals(name)) {
						taskListToReturn[j] = taskListFromJSON[i];
						j++;
					}
				}
			}

			return taskListToReturn;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Task[] readAllTask() {
		try {
			Gson gson = new Gson();
			Task[] taskListFromJSON;
			
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			
			return taskListFromJSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeTask(Task task) {
		try {
			Gson gson = new Gson();
			Task[] taskListFromJSON;
			ArrayList<Task> taskListTransition;
			Task[] taskListToReturn;
			
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			
			if (taskListFromJSON == null) {
				taskListFromJSON = EMPTY_TASK;
			}
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(taskListFromJSON));
			
			taskListTransition.add(task);
			
			taskListToReturn = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			gson.toJson(taskListToReturn, bufferedWriter);
			bufferedWriter.flush();
			// bufferedWriter.write(gson.toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void removeTask(Task task) {
		
	}
}