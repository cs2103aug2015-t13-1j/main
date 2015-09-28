import java.io.File;
import java.io.IOException;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import com.google.gson.Gson;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	private final String DIRECTORY = "./";
	private final String FILE_NAME = "TaskStorage";
	private final String FILE_TYPE = ".json";
	private FileReader fileReader;
	private FileWriter fileWriter;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	public StorageManager() {
	}

	public void openStorage() {
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

	public void closeStorage() {
		try {
			bufferedReader.close();
			bufferedWriter.close();
			fileReader.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Task[] readTask(String name) {
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

	public Task[] readAllTask() {
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

	public void writeTask(Task task) {
		try {
			Gson gson = new Gson();
			Task[] taskListFromJSON;
			
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			taskListFromJSON[taskListFromJSON.length] = task;
			
			gson.toJson(taskListFromJSON);
			
			bufferedWriter.write(gson.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}