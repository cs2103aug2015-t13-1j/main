import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	private final String FILE_NAME = "TaskStorage";
	private final String FILE_TYPE = ".json"
	private final String DIRECTORY = "./";
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;

	public StorageManager() {
	}

	public boolean openStorage() {
		try {
			File file = new File(DIRECTORY + FILE_NAME + FILE_TYPE);

			if (!file.exists()) {
				file.createNewFile();
			}

			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean closeStorage() {
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean writeTask(Task task) {


		try {
			bufferedWriter.write(task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Task[] readTask(String name) {

	}

	public Task[] readAllTask() {

	}
}