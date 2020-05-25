package dk.erst.delis.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class TestCaseProvider {

	private static String testFolder = "../delis-resources/examples/xml";

	public static List<TestCaseData> getTestCaseList() throws IOException {
		Path root = Paths.get(testFolder);

		List<TestCaseData> testCaseList = new ArrayList<TestCaseProvider.TestCaseData>();

		Files.walk(root).forEach(path -> {
			File testFile = path.toFile();
			if (testFile.isDirectory()) {
				return;
			}

			TestCaseData tcd = new TestCaseData(testFile);
			tcd.errorExpected = path.toString().contains("error");
			testCaseList.add(tcd);
		});

		return testCaseList;
	}

	@Getter
	public static class TestCaseData {
		private boolean errorExpected;
		private File file;

		public TestCaseData(File file) {
			this.file = file;
		}

		public InputStream getInputStream() throws IOException {
			return new FileInputStream(file);
		}

		public String getDescription() {
			return file.getName();
		}

	}
}
