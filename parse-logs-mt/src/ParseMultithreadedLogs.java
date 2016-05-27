import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ParseMultithreadedLogs {

	public static void main(String[] args) {
		List<HashMap<String, String>> listMap = new ArrayList<HashMap<String, String>>();
		for (String arg : args) {
			HashMap<String, String> returnMap = returnMapFromFile(arg);
			listMap.add(returnMap);
		}
		writeToFileFromMap(listMap);
	}

	public static HashMap<String, String> returnMapFromFile(String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			String strLine = null;
			HashMap<String, String> map = new HashMap<String, String>();
			String prevThread = null;
			/* read log line by line */
			while ((strLine = br.readLine()) != null) {
				/* parse strLine to obtain what you want */
				String thread = null;
				if (strLine.contains("ExecuteThread")) {
					thread = strLine.substring(strLine.indexOf("Thread:") + 8,strLine.indexOf("for"));
					prevThread = thread;
				} else {
					thread = prevThread;
					strLine = map.get(thread) + "\n" + strLine;
				}
				if (map.containsKey(thread)) {
					String prevLog = map.get(thread);
					prevLog = prevLog + "\n" + strLine;
					map.put(thread, prevLog);
				} else {
					map.put(thread, strLine);
				}
			}
			br.close();
			return map;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return null;
	}

	public static void printMap(HashMap<String, String> map) {
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = map.get(key);
			System.out.println(key + " " + value);
			// writeToFile(key,value);
		}
	}

	public static void writeToFileFromMap(List<HashMap<String, String>> mapList) {
		try {
			for (HashMap<String, String> map : mapList) {
				Iterator iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = map.get(key);
					System.out.println(key + " " + value);

					String fileName = key + "thread.log";
					File file = new File(fileName);

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					} else {
						value = "\n" + value;
					}

					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(value);
					bw.close();
				}
			}
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
