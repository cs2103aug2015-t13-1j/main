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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	// Specificiation for TaskStorage.json
	private static String STORAGE_DIRECTORY = "";
	private static String STORAGE_NAME = "";
	private static String STORAGE_TYPE = "";
	// Specificiation for Default TaskStorage.json
	private static String DEFAULT_STORAGE_DIRECTORY = "./";
	private static String DEFAULT_STORAGE_NAME = "TaskStorage";
	private static String DEFAULT_STORAGE_TYPE = ".json";
	// Specificiation for StorageInformation.json
	private static String INFORMATION_DIRECTORY = "./";
	private static String INFORMATION_NAME = "StorageInformation";
	private static String INFORMATION_TYPE = ".json";
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
	private static final Logger log = Logger.getLogger(Ui.LOG_NAME);
	
	// Error Messages
	public static final String ERROR_FILE_READER_UNINITIALIZED = "File reader could not be initialized.";
	public static final String ERROR_FILE_WRITER_UNINITIALIZED = "File writer could not be initialized.";
	public static final String ERROR_FILE_READER_NOT_CLOSED = "File reader could not be closed.";
	public static final String ERROR_FILE_WRITER_NOT_CLOSED = "File writer could not be closed.";
	public static final String ERROR_INFORMATION_READER_NOT_CLOSED = "Information reader could not be closed.";
	public static final String ERROR_INFORMATION_WRITER_NOT_CLOSED = "Information writer could not be closed.";
	public static final String ERROR_INFORMATION_READER_AND_WRITER_NOT_CLOSED = "Information reader and writer could not be closed.";
	public static final String ERROR_TASK_LIST_UNINITIALIZED = "Task list could not be initialized.";
	public static final String ERROR_CURRENT_STORAGE_NOT_DELETED = "The Original Storage File could not be deleted.";
	public static final String ERROR_TASK_NOT_WRITTEN = "Task could not be written.";
	public static final String ERROR_TASK_NOT_REMOVED = "Task could not be removed.";
	public static final String ERROR_EMPTY_TASK_LIST = "You currently do not have any tasks saved.";
	public static final String ERROR_TASK_NOT_FOUND = "\"%s\" was not found."; 
	
	/**
	 * This method constructs the StorageManager
	 */
	public StorageManager() {
		log.log(Level.INFO, "StorageManager is successfully initialized.\n");
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
				if (file.createNewFile()) {
					log.log(Level.INFO, "File does not exist and is created.\n");
				} else {
					log.log(Level.WARNING, "File could not be created.\n");
				}
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
			log.log(Level.WARNING, "Error in opening storage.\n");
			throw new Exception(e.getMessage());
		}
		
		log.log(Level.INFO, "Storage is successfully set to open.\n");
	}

	/**
	 * This method closes the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	public void closeStorage() throws Exception {
		assert(file.exists());
		
		try {
			closeReader();
			closeWriter();
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in closing storage.\n");
			throw new Exception(e.getMessage());
		}
		
		file = null;
		
		log.log(Level.INFO, "Storage is successfully set to closed.\n");
	}

	/**
	 * Following methods sets/closes the reader/writer for TaskStorage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private void setReader() throws Exception {
		assert(file.exists());
		
		try {
			fileReader = new FileReader(file.getAbsoluteFile());
			bufferedReader = new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in setting reader.\n");
			throw new Exception(ERROR_FILE_READER_UNINITIALIZED);
		}
		
		log.log(Level.INFO, "Reader is successfully set.\n");
	}

	private void setWriterWithAppend() throws Exception {
		assert(file.exists());
		
		try {
			fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in setting writer.\n");
			throw new Exception(ERROR_FILE_WRITER_UNINITIALIZED);
		}
		
		log.log(Level.INFO, "Writer is successfully set with append.\n");
	}

	private void setWriterWithoutAppend() throws Exception {
		assert(file.exists());
		
		try {
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in setting writer.\n");
			throw new Exception(ERROR_FILE_WRITER_UNINITIALIZED);
		}
		
		log.log(Level.INFO, "Writer is successfully set without append.\n");
	}

	private void closeReader() throws Exception {
		assert(file.exists());
		
		try {
			bufferedReader.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in closing reader.\n");
			throw new Exception(ERROR_FILE_READER_NOT_CLOSED);
		}
		
		bufferedReader = null;
		fileReader = null;
		
		log.log(Level.INFO, "Reader is successfully closed.\n");
	}

	private void closeWriter() throws Exception {
		assert(file.exists());
		
		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Error in closing writer.\n");
			throw new Exception(ERROR_FILE_WRITER_NOT_CLOSED);
		}
		
		bufferedWriter = null;
		fileWriter = null;
		
		log.log(Level.INFO, "Writer is successfully closed.\n");
	}

	/**
	 * This method initializes the Storage
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private void initializeStorage() throws Exception {
		assert(file.exists());
		
		File informationFile = new File(INFORMATION_DIRECTORY + INFORMATION_NAME + INFORMATION_TYPE);	
		FileReader informationFileReader;
		BufferedReader informationBufferedReader;
		
		if (!informationFile.exists()) {
			log.log(Level.INFO, "Storage Information does not exist.\n");
			informationFile.createNewFile();

			// Initiate Reader / Writer for StorageInformation.json
			informationFileReader = new FileReader(informationFile.getAbsoluteFile());
			informationBufferedReader = new BufferedReader(informationFileReader);
			FileWriter informationFileWriter = new FileWriter(informationFile.getAbsoluteFile());
			BufferedWriter informationBufferedWriter = new BufferedWriter(informationFileWriter);
			
			// Create and set value of Storage Information
			StorageInformation initialStorageInformation = new StorageInformation();
			initialStorageInformation.setFileDirectory(DEFAULT_STORAGE_DIRECTORY);
			initialStorageInformation.setFileName(DEFAULT_STORAGE_NAME);
			initialStorageInformation.setFileType(DEFAULT_STORAGE_TYPE);

			// Write to StorageInformation.json as soon as append is set to false
			gson.toJson(initialStorageInformation, informationBufferedWriter);
			informationBufferedWriter.flush();
			
			try {
				// Close Writer
				informationBufferedWriter.close();
				informationFileWriter.close();
			} catch (IOException e) {
				log.log(Level.WARNING, "Information writer could not be closed.\n");
				throw new Exception(ERROR_INFORMATION_WRITER_NOT_CLOSED);
			}
			
		} else {
			log.log(Level.INFO, "Storage Information already exists.\n");
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
			log.log(Level.WARNING, "Information reader could not be closed.\n");
			throw new Exception(ERROR_INFORMATION_READER_NOT_CLOSED);
		}
		
		log.log(Level.INFO, "Storage is successfully initialized.\n");
	}

	/**
	 * This method reads the task list from JSON and returns that task list
	 * 
	 * @throws Exception	if the task was unable to be written
	 */
	private Task[] initiateTaskList() throws Exception {
		Task[] taskListFromJSON;
		
		try {
			taskListFromJSON = gson.fromJson(bufferedReader, Task[].class);
			
			if (taskListFromJSON == null) {
				taskListFromJSON = EMPTY_TASK;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Task list could not be initialized.\n");
			throw new Exception(ERROR_TASK_LIST_UNINITIALIZED);
		}
		
		log.log(Level.INFO, "Task list succesfully initialized.\n");
		return taskListFromJSON;
	}

	/**
	 * This method changes location of the Storage
	 * 
	 * @param directory
	 * @throws Exception	if the task was unable to be written
	 */
	public boolean changeStorageLocation(String directory) throws Exception {
		assert(file.exists());
		assert(directory.length() >= 2);
		assert((directory.charAt(directory.length() - 1) == '\\') || (directory.charAt(directory.length() - 1) == '/') == true);
		
		String oldDirecotry = STORAGE_DIRECTORY;
		String oldName = STORAGE_NAME;
		String oldType = STORAGE_TYPE;
		
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
		
		// Create a file to check if it exists and if it can be created at input path
		File check = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		if(!check.exists()) {
			if (!check.createNewFile()) {
				// Write back to original StorageInformation.json as new StorageInformation file cannot be created
				gson.toJson(storageInformationFromJson, informationBufferedWriter);
				informationBufferedWriter.flush();
				
				STORAGE_DIRECTORY = oldDirecotry;
				STORAGE_NAME = oldName;
				STORAGE_TYPE = oldType;
				
				try {
					informationBufferedWriter.close();
					informationBufferedReader.close();
					informationFileWriter.close();
					informationFileReader.close();
				} catch (IOException e) {
					log.log(Level.WARNING, "Information reader and writer could not be closed.\n");
					throw new Exception(ERROR_INFORMATION_READER_AND_WRITER_NOT_CLOSED);
				}
				
				log.log(Level.WARNING, "Not able to create file at specified location, written back to original StorageInformation.json");
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
			throw new Exception(ERROR_CURRENT_STORAGE_NOT_DELETED);
		}
		
		// Set directory
		file = new File(STORAGE_DIRECTORY + STORAGE_NAME + STORAGE_TYPE);
		
		try {
			informationBufferedWriter.close();
			informationBufferedReader.close();
			informationFileWriter.close();
			informationFileReader.close();
		} catch (IOException e) {
			log.log(Level.WARNING, "Information reader and writer could not be closed.\n");
			throw new Exception(ERROR_INFORMATION_READER_AND_WRITER_NOT_CLOSED);
		}
		
		if (!file.exists()) {
			file.createNewFile();
		}

		setReader();
		setWriterWithoutAppend();
		
		// Write to TaskStorage.json as soon as append is set to false
		gson.toJson(TASK_LIST, bufferedWriter);
		bufferedWriter.flush();
		
		log.log(Level.INFO, "Storage location successfully changed.\n");
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

		log.log(Level.INFO, "Returning all the tasks in a sorted order.\n");
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
		assert(file.exists());
		
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
			log.log(Level.WARNING, "Task could not be written.\n");
			throw new Exception(ERROR_TASK_NOT_WRITTEN);
		}
		
		log.log(Level.INFO, "Successfully written to task list.\n");
	}
	
	//@@author A0145732H
	/**
	 * This method removes a given task from the Storage
	 * 
	 * @param task
	 * @throws Exception	if the task was unable to be removed
	 */
	public void removeTask(Task task) throws Exception {
		assert(file.exists());
		
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
			log.log(Level.WARNING, "Task could not be removed.\n");
			throw new Exception(ERROR_TASK_NOT_REMOVED);
		}
		
		if (!isRemoved) {
			throw new Exception(String.format(ERROR_TASK_NOT_FOUND, task.getName()));
		}
		
		log.log(Level.INFO, "Successfully removed task from task list.\n");
	}

	/**
	 * This method updates a task in the task list with the new task in Storage
	 * 
	 * @param oldTask		the task to search for and update
	 * @param newTask		the updated version of the task to replace the old task
	 * @throws Exception	if there are no tasks or if the old task was not found
	 */
	public void updateTask(Task oldTask, Task newTask) throws Exception {
		assert(file.exists());
		
		if (TASK_LIST.length == 0) {
			throw new Exception(ERROR_EMPTY_TASK_LIST);
		}

		removeTask(oldTask);
		writeTask(newTask);
		
		log.log(Level.INFO, "Successfully updated task from task list.\n");
	}

	//@@author A0100081E
	/**
	 * This method clears the task list from the Storage
	 * 
	 * @throws Exception	if the task was unable to be cleared
	 */
	public void clearAllTasks() throws Exception {
		assert(file.exists());
		
		TASK_LIST = EMPTY_TASK;
		
		closeWriter();
		setWriterWithoutAppend();
		gson.toJson(EMPTY_TASK, bufferedWriter);
		
		log.log(Level.INFO, "Successfully cleared task from task list.\n");
	}
	
	/**
	 * Following are public methods that accesses private attributes in StorageManager
	 */
	public String getStorageDirectory() {
		return StorageManager.STORAGE_DIRECTORY;
	}
	
	public String getStorageName() {
		return StorageManager.STORAGE_NAME;
	}
	
	public String getStorageType() {
		return StorageManager.STORAGE_TYPE;
	}
	
	public String getInformationDirectory() {
		return StorageManager.INFORMATION_DIRECTORY;
	}

	public String getInformationName() {
		return StorageManager.INFORMATION_NAME;
	}

	public String getInformationType() {
		return StorageManager.INFORMATION_TYPE;
	}

	public void setInformationDirectory(String directory) {
		StorageManager.INFORMATION_DIRECTORY = directory;
	}

	public void setInformationName(String name) {
		StorageManager.INFORMATION_NAME = name;
	}

	public void setInformationType(String type) {
		StorageManager.INFORMATION_TYPE = type;
	}

	public void setDefaultDirectory(String directory) {
		StorageManager.DEFAULT_STORAGE_DIRECTORY = directory;
	}

	public void setDefaultName(String name) {
		StorageManager.DEFAULT_STORAGE_NAME = name;
	}

	public void setDefaultType(String type) {
		StorageManager.DEFAULT_STORAGE_TYPE = type;
	}
	
	public File getStorageFile() {
		assert(file.exists());
		
		return StorageManager.file;
	}
}