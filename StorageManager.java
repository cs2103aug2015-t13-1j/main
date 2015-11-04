//@@author
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	private static String STORAGE_DIRECTORY;
	private static String STORAGE_NAME;
	private static String STORAGE_TYPE;
	private static final String INFORMATION_DIRECTORY = "./";
	private static final String INFORMATION_NAME = "TaskInformation";
	private static final String INFORMATION_TYPE = ".json";
	private static File file;
	private static FileReader fileReader;
	private static FileWriter fileWriter;
	private static BufferedReader bufferedReader;
	private static BufferedWriter bufferedWriter;
	private static Task[] TASK_LIST = {};
	private static final Task[] EMPTY_TASK = {};

	public StorageManager() {
	}
	
	public static void initializeStorage() throws FileNotFoundException {
		File informationFile = new File(INFORMATION_DIRECTORY + INFORMATION_NAME + INFORMATION_TYPE);	
		FileReader informationFileReader = new FileReader(informationFile.getAbsoluteFile());
		BufferedReader informationBufferedReader = new BufferedReader(informationFileReader);
		
		Gson gson = new Gson();
		StorageInformation storageInformationFromJson;		
		storageInformationFromJson = gson.fromJson(informationBufferedReader, StorageInformation.class);
		
		STORAGE_DIRECTORY = storageInformationFromJson.getFileDirectory();
		STORAGE_NAME = storageInformationFromJson.getFileName();
		STORAGE_TYPE = storageInformationFromJson.getFileType();
		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		try {
			informationFileReader.close();
			informationBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openStorage() {
		try {
			initializeStorage();
			if (!file.exists()) {
				file.createNewFile();
			}

			fileReader = new FileReader(file.getAbsoluteFile());
			fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			bufferedReader = new BufferedReader(fileReader);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			TASK_LIST = initiateTaskList();	
			
			// Set append to false because, we want to be overwriting
			bufferedWriter.close();
			fileWriter.close();
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
			
			// Write to TaskStorage.json as soon as setting fileWrite's append to false
			Gson gson = new Gson();
			gson.toJson(TASK_LIST, bufferedWriter);
			bufferedWriter.flush();
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
			bufferedWriter.close();
			bufferedReader.close();
			fileWriter.close();
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean changeStorageLocation(String directory) throws Exception {
		STORAGE_DIRECTORY = directory;
		
		// Create reader for Storage Information
		File informationFile = new File(INFORMATION_DIRECTORY + INFORMATION_NAME + INFORMATION_TYPE);	
		FileReader informationFileReader = new FileReader(informationFile.getAbsoluteFile());
		FileWriter informationFileWriter = new FileWriter(informationFile.getAbsoluteFile(), true);
		BufferedReader informationBufferedReader = new BufferedReader(informationFileReader);
		BufferedWriter informationBufferedWriter = new BufferedWriter(informationFileWriter);
		
		// Read Storage Information
		Gson gson = new Gson();
		StorageInformation storageInformationFromJson;		
		storageInformationFromJson = gson.fromJson(informationBufferedReader, StorageInformation.class);
		
		// Clear Storage Information
		informationFileWriter = new FileWriter(informationFile.getAbsoluteFile());
		informationBufferedWriter = new BufferedWriter(informationFileWriter);
		
		// Set directory
		STORAGE_DIRECTORY = directory;
		STORAGE_NAME = storageInformationFromJson.getFileName();
		STORAGE_TYPE = storageInformationFromJson.getFileType();
		
		File check = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		// Check if file exists
		if(!check.exists()) {
			// Change Storage Information and Add back to StorageInformation.json
			gson.toJson(storageInformationFromJson, informationBufferedWriter);
			informationBufferedWriter.flush();
			
			try {
				informationBufferedWriter.close();
				informationBufferedReader.close();
				informationFileWriter.close();
				informationFileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
		
		// Change Storage Information and Add back to StorageInformation.json
		storageInformationFromJson.setFileDirectory(directory);
		gson.toJson(storageInformationFromJson, informationBufferedWriter);
		informationBufferedWriter.flush();
			
		if(!file.delete()) {
			throw new Exception("File has not been deleted");
		}
		
		// Set directory
		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		// Close reader for Storage Information
		try {
			informationBufferedWriter.close();
			informationBufferedReader.close();
			informationFileWriter.close();
			informationFileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Create new file
		if (!file.exists()) {
			file.createNewFile();
		}

		// Reset settings
		bufferedWriter.close();
		bufferedReader.close();
		fileWriter.close();
		fileReader.close();
		fileReader = new FileReader(file.getAbsoluteFile());
		fileWriter = new FileWriter(file.getAbsoluteFile());
		bufferedReader = new BufferedReader(fileReader);
		bufferedWriter = new BufferedWriter(fileWriter);
		
		// Write to TaskStorage.json as soon as setting fileWrite's append to false
		gson.toJson(TASK_LIST, bufferedWriter);
		bufferedWriter.flush();
		
		return true;
	}
	
//	public static void changeStorageName(String name) { SKSK UNDONE
//		STORAGE_NAME = name;
//		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
//	}
	
	//@@author A0145732H
	public static ArrayList<Task> readAllTasks() {
		ArrayList<Task> taskArrayList = new ArrayList<Task>();
		taskArrayList.addAll(Arrays.asList(TASK_LIST));
		taskArrayList.sort(null);
		return taskArrayList;
	}

	//@@author
	public static void writeTask(Task task) {
		try {
			Gson gson = new Gson();
			ArrayList<Task> taskListTransition;
			Task[] taskListToReturn;
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(TASK_LIST));
			
			taskListTransition.add(task);
			
			taskListToReturn = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			TASK_LIST = new Task[taskListToReturn.length];
			TASK_LIST = taskListToReturn;
			
			// hacky
			bufferedWriter.close();
			fileWriter.close();
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
			
			gson.toJson(taskListToReturn, bufferedWriter);
			bufferedWriter.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	//@@author A0145732H
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
			
			// hacky
			bufferedWriter.close();
			fileWriter.close();
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
			
			gson.toJson(taskListToUpdate, bufferedWriter);
			bufferedWriter.flush();
			
		} catch (Exception e) {
			// TODO is this what would throw the exception?
			// if so we should re-add the removed task to the local copy to reflect the file
			isRemoved = false;
			throw new Exception("Error saving changes to file.");
		}
		
		if (!isRemoved) {
			throw new Exception("\"" + task.getName() + "\" was not found.");
		}
	}
	
	//@@author
	public static void clearTask() throws Exception {
		Gson gson = new Gson();

		TASK_LIST = EMPTY_TASK;
		
		// hacky
		bufferedWriter.close();
		fileWriter.close();
		fileWriter = new FileWriter(file.getAbsoluteFile());
		bufferedWriter = new BufferedWriter(fileWriter);
		gson.toJson("", bufferedWriter);
	}

	//@@author A0145732H
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
		removeTask(oldTask);
		writeTask(newTask);
	}
}