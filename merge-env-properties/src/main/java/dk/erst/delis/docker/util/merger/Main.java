package dk.erst.delis.docker.util.merger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
 * Simple utility to overwrite some values in properties file by environment variables.
 */
public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: <ENV_VARIABLES_PREFIX> <PATH_TO_PROPERTIES_FILE>");
			System.exit(-1);
			return;
		}

		String prefix = args[0];
		String propertiesFile = args[1];

		Map<String, String> environmentMap = System.getenv();

		File file = new File(propertiesFile);
		System.out.println("Merging properties file " + canonicalPathSafe(file) + " with environment by variables with prefix " + prefix);
		if (!file.exists() || !file.isFile()) {
			System.err.println("File " + file.getAbsolutePath() + " does not exists or is not a file");
			System.exit(-2);
			return;
		}

		Properties properties = new Properties();
		try (FileInputStream is = new FileInputStream(file)) {
			properties.load(is);
		} catch (Exception e) {
			System.out.println("Cannot read given file as java.util.Properties file: " + canonicalPathSafe(file));
			e.printStackTrace();
			System.exit(-3);
			return;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(propertiesFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-4);
			return;
		}

		PropertiesMerger m = new PropertiesMerger();
		List<String> resultList = m.merge(lines, environmentMap, prefix);

		if (!resultList.isEmpty()) {
			String propertiesFileBackup = propertiesFile + ".bak";
			try (FileOutputStream fos = new FileOutputStream(propertiesFileBackup)) {
				Files.copy(Paths.get(propertiesFile), fos);
				System.out.println("Created backup of file: " + canonicalPathSafe(new File(propertiesFileBackup)));
			} catch (IOException e) {
				System.err.println("Failed to create a backup for file " + propertiesFile);
				e.printStackTrace();
				return;
			}
			try {
				Files.write(Paths.get(propertiesFile), resultList);
				System.out.println(String.format("Saved %d lines to file %s", resultList.size(), canonicalPathSafe(new File(propertiesFile))));
			} catch (IOException e) {
				System.err.println("Failed to write modified properties back to file " + propertiesFile);
				e.printStackTrace();
				System.exit(-5);
				return;
			}
		}
	}

	private static String canonicalPathSafe(File file) {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			return file.getAbsolutePath();
		}
	}

}
