//@@author A0100081E
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
	// Specificiation for TaskStorage.json
	private static String STORAGE_DIRECTORY;
	private static String STORAGE_NAME;
	private static String STORAGE_TYPE;
	// Specificiation for Default TaskStorage.json
	private static final String DEFAULT_STORAGE_DIRECTORY = "./";
	private static final String DEFAULT_STORAGE_NAME = "TaskStorage";
	private static final String DEFAULT_STORAGE_TYPE = ".json";
	// Specificiation for StorageInformation.json
	private static final String INFORMATION_DIRECTORY = "./";
	private static final String INFORMATION_NAME = "StorageInformation";
	private static final String INFORMATION_TYPE = ".json";
	// Variables for File
	private static File file;
	private static FileReader fileReader;
	private static FileWriter fileWriter;
	private static BufferedReader bufferedReader;
	private static BufferedWriter bufferedWriter;
	// Variables for Task
	private static Task[] TASK_LIST = {};
	private static final Task[] EMPTY_TASK = {};
	// Gson Variable
	private static Gson gson = new Gson();

	/**
	 * This method constructs the StorageManager
	 */
	public StorageManager() {
	}

	/**
	 * This method opens the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	public void openStorage() throws Exception {
		try {
			initializeStorage();

			if (!file.exists()) {
				file.createNewFile();
			}

			// Set append to false because system should read the data inside TaskStorage.json
			setReader();
			setWriterWithAppend();
			
			TASK_LIST = initiateTaskList();	
			
			// Set append to false because system should be overwriting to the TaskStorage.json
			closeWriter();
			setWriterWithoutAppend();
			
			// Write to TaskStorage.json as soon as setting fileWrite's append to false
			gson.toJson(TASK_LIST, bufferedWriter);
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * This method closes the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	public void closeStorage() throws Exception {
		try {
			closeReader();
			closeWriter();
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Following methods sets/closes the reader/writer for TaskStorage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private void setReader() throws Exception {
		try {
			fileReader = new FileReader(file.getAbsoluteFile());
			bufferedReader = new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			throw new Exception("File reader could not be initialized.");
		}
	}

	private void setWriterWithAppend() throws Exception {
		try {
			fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (FileNotFoundException e) {
			throw new Exception("File writer could not be initialized.");
		}
	}

	private void setWriterWithoutAppend() throws Exception {
		try {
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (FileNotFoundException e) {
			throw new Exception("File writer could not be initialized.");
		}
	}

	private void closeReader() throws Exception {
		try {
			bufferedReader.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			throw new Exception("File reader could not be closed.");
		}
	}

	private void closeWriter() throws Exception {
		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			throw new Exception("File writer could not be closed.");
		}
	}

	/**
	 * This method initializes the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private void initializeStorage() throws Exception {
		File informationFile = new File(INFORMATION_DIRECTORY + INFORMATION_NAME + INFORMATION_TYPE);	
		FileReader informationFileReader;
		BufferedReader informationBufferedReader;
		
		if (!informationFile.exists()) {
			informationFile.createNewFile();

			// Initiate Reader / Writer for StorageInformation.json
			informationFileReader = new FileReader(informationFile.getAbsoluteFile());
			informationBufferedReader = new BufferedReader(informationFileReader);
			FileWriter informationFileWriter = new FileWriter(informationFile.getAbsoluteFile());
			BufferedWriter informationBufferedWriter = new BufferedWriter(informationFileWriter);
			
			// Create and set value fo Storage Information
			StorageInformation initialStorageInformation = new StorageInformation();
			initialStorageInformation.setFileDirectory(DEFAULT_STORAGE_DIRECTORY);
			initialStorageInformation.setFileName(DEFAULT_STORAGE_NAME);
			initialStorageInformation.setFileType(DEFAULT_STORAGE_TYPE);

			// Write to StorageInformation.json as soon as append is set to false
			gson.toJson(initialStorageInformation, informationBufferedWriter);
			informationBufferedWriter.flush();
			
			// Close Writer
			informationBufferedWriter.close();
			informationFileWriter.close();
		} else {
			// Initiate Reader
			informationFileReader = new FileReader(informationFile.getAbsoluteFile());
			informationBufferedReader = new BufferedReader(informationFileReader);
		}
		
		// Read Storage Information
		StorageInformation storageInformationFromJson;		
		storageInformationFromJson = gson.fromJson(informationBufferedReader, StorageInformation.class);
		
		// Set file with variables from StorageInformation.json
		STORAGE_DIRECTORY = storageInformationFromJson.getFileDirectory();
		STORAGE_NAME = storageInformationFromJson.getFileName();
		STORAGE_TYPE = storageInformationFromJson.getFileType();
		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		try {
			// Close Reader
			informationFileReader.close();
			informationBufferedReader.close();
		} catch (IOException e) {
			throw new Exception("Information reader could not be closed.");
		}
	}

	/**
	 * This method reads the task list from JSON and returns that task list
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private Task[] initiateTaskList() throws Exception {
		try {
			Task[] taskListFromJSON;
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			
			if (taskListFromJSON == null) {
				taskListFromJSON = EMPTY_TASK;
			}
			
			return taskListFromJSON;
		} catch (Exception e) {
			throw new Exception("Task list could not be initialized.");
		}
	}

	/**
	 * This method changes location of the Storage
	 * 
	 * @param directory
	 * @throws Exception	if the task was unable to be written
	 */
	public boolean changeStorageLocation(String directory) throws Exception {
		STORAGE_DIRECTORY = directory;
		
		// Initiate Reader / Writer for StorageInformation.json
		File informationFile = new File(INFORMATION_DIRECTORY + INFORMATION_NAME + INFORMATION_TYPE);
		FileReader informationFileReader = new FileReader(informationFile.getAbsoluteFile());
		FileWriter informationFileWriter = new FileWriter(informationFile.getAbsoluteFile(), true);
		BufferedReader informationBufferedReader = new BufferedReader(informationFileReader);
		BufferedWriter informationBufferedWriter = new BufferedWriter(informationFileWriter);
		
		// Read Storage Information
		StorageInformation storageInformationFromJson;		
		storageInformationFromJson = gson.fromJson(informationBufferedReader, StorageInformation.class);
		
		// Clear Storage Information
		informationBufferedWriter.close();
		informationFileWriter.close(); 
		informationFileWriter = new FileWriter(informationFile.getAbsoluteFile());
		informationBufferedWriter = new BufferedWriter(informationFileWriter);
		
		// Set file with variables from StorageInformation.json and directory as input
		STORAGE_DIRECTORY = directory;
		STORAGE_NAME = storageInformationFromJson.getFileName();
		STORAGE_TYPE = storageInformationFromJson.getFileType();
		
		// Create a file to check if it exists and if is can be created at input path
		File check = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		if(!check.exists()) {
			if (!check.createNewFile()) {
				// Write back to original StorageInformation.json as new StorageInformation file cannot be created
				gson.toJson(storageInformationFromJson, informationBufferedWriter);
				informationBufferedWriter.flush();
				
				try {
					informationBufferedWriter.close();
					informationBufferedReader.close();
					informationFileWriter.close();
					informationFileReader.close();
				} catch (IOException e) {
					throw new Exception("Information reader and writer could not be closed.");
				}
				
				return false;
			}
		}
		
		// Change Storage Information and Add back to StorageInformation.json
		storageInformationFromJson.setFileDirectory(directory);
		gson.toJson(storageInformationFromJson, informationBufferedWriter);
		informationBufferedWriter.flush();
			
		// close all file streams before deleting the file
		closeReader();
		closeWriter();


		if(!file.delete()) {
			throw new Exception("The Original Storage File could not be deleted.");
		}
		
		// Set directory
		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		try {
			informationBufferedWriter.close();
			informationBufferedReader.close();
			informationFileWriter.close();
			informationFileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!file.exists()) {
			file.createNewFile();
		}

		setReader();
		setWriterWithoutAppend();
		
		// Write to TaskStorage.json as soon as append is set to false
		gson.toJson(TASK_LIST, bufferedWriter);
		bufferedWriter.flush();
		
		return true;
	}
	
	//@@author A0145732H
	/**
	 * This method reads all task existing in the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	public ArrayList<Task> readAllTasks() {
		ArrayList<Task> taskArrayList = new ArrayList<Task>();

		taskArrayList.addAll(Arrays.asList(TASK_LIST));
		taskArrayList.sort(null);

		return taskArrayList;
	}

	//@@author A0100081E
	/**
	 * This method writes a given task to the Storage
	 * 
	 * @param task
	 * @throws Exception	if the task was unable to be written
	 */
	public void writeTask(Task task) throws Exception {
		try {
			ArrayList<Task> taskListTransition;
			Task[] taskListToReturn;
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(TASK_LIST));
			
			taskListTransition.add(task);
			
			taskListToReturn = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			TASK_LIST = new Task[taskListToReturn.length];
			TASK_LIST = taskListToReturn;
			
			closeWriter();
			setWriterWithoutAppend();
			
			gson.toJson(taskListToReturn, bufferedWriter);
			bufferedWriter.flush();
			
		} catch (Exception e) {
			throw new Exception("Task could not be written");
		}
	}
	
	//@@author A0145732H
	/**
	 * This method removes a given task from the Storage
	 * 
	 * @param task
	 * @throws Exception	if the task was unable to be removed
	 */
	public void removeTask(Task task) throws Exception {
		boolean isRemoved = false;
		try {
			ArrayList<Task> taskListTransition;
			Task[] taskListToUpdate;
			
			taskListTransition = new ArrayList<Task> (Arrays.asList(TASK_LIST));
			
			isRemoved = taskListTransition.remove(task);
			
			taskListToUpdate = taskListTransition.toArray(new Task[taskListTransition.size()]);
			
			TASK_LIST = new Task[taskListToUpdate.length];
			TASK_LIST = taskListToUpdate;
			
			closeWriter();
			setWriterWithoutAppend();
			
			gson.toJson(taskListToUpdate, bufferedWriter);
			bufferedWriter.flush();
			
		} catch (Exception e) {
			isRemoved = false;
			throw new Exception("Error saving changes to file.");
		}
		
		if (!isRemoved) {
			throw new Exception("\"" + task.getName() + "\" was not found.");
		}
	}

	/**
	 * This method updates a task in the task list with the new task in Storage
	 * 
	 * @param oldTask		the task to search for and update
	 * @param newTask		the updated version of the task to replace the old task
	 * @throws Exception	if there are no tasks or if the old task was not found
	 */
	public void updateTask(Task oldTask, Task newTask) throws Exception {
		if (TASK_LIST.length == 0) {
			throw new Exception("You currently do not have any tasks saved.");
		}

		removeTask(oldTask);
		writeTask(newTask);
	}

	//@@author A0100081E
	/**
	 * This method clears the task list from the Storage
	 * 
	 * @throws Exception	if the task was unable to be cleared
	 */
	public void clearTask() throws Exception {
		TASK_LIST = EMPTY_TASK;
		
		closeWriter();
		setWriterWithoutAppend();
		gson.toJson("", bufferedWriter);
	}
}