import java.io.File;
import java.io.IOException;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * StorageManager is a class that read/write/delete appropriate task information to the Storage.
 */
public class StorageManager {
	private final String DIRECTORY = "./";
	private final String FILE_NAME = "TaskStorage";
	private final String FILE_TYPE = ".json"
	private FileReader fileReader;
	private FileWriter fileWriter;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	public StorageManager() {
	}

	public boolean openStorage() {
		try {
			File file = new File(DIRECTORY + FILE_NAME + FILE_TYPE);

			if (!file.exists()) {
				file.createNewFile();
			}

			fileReader = new FileReader(file.getAbsoluteFile());
			fileWriter = new FileWriter(file.getAbsoluteFile());
			bufferedReader = new BufferedWriter(fileReader);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean closeStorage() {
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
		JSONParser jsonParser = new JSONParser();

		try {
			Object obj = jsonParser.parse(bufferedReader);

			JSONArray jsonArray = (JSONArray) obj; 
			Task[] taskList = new Task[jsonArray.length()];

			if (jsonArray != null) { 
				for (int i = 0; i < jsonArray.length(); i++) {
					if (jsonArray.get(i).name.equals(name)) {
						taskList[i] = jsonArray.get(i); // SKSK: Maybe need to convert Object (jsonArray.get(i)) to Task
					}
				}
			}

			return taskList;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Task[] readAllTask() {
		JSONParser jsonParser = new JSONParser();

		try {
			Object obj = jsonParser.parse(bufferedReader);

			JSONArray jsonArray = (JSONArray) obj; 
			Task[] taskList = new Task[jsonArray.length()];

			if (jsonArray != null) { 
				for (int i = 0; i < jsonArray.length(); i++) {
					taskList[i] = jsonArray.get(i); // SKSK: Maybe need to convert Object (jsonArray.get(i)) to Task
				}
			}

			return taskList;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTask(Task task) {
		JSONParser jsonParser = new JSONParser();

		try {
			Object obj = jsonParser.parse(bufferedReader);

			JSONArray jsonArray = (JSONArray) obj; 
			jsonArray.add(task);

			bufferedWriter.write(jsonArray.toJSONString());

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}