package dk.erst.delis.task.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDocumentUtil {

	public static Path createTestFile(TestDocument testDocument) throws IOException {
		InputStream inputStream = testDocument.getInputStream();
		String prefix = testDocument.name();
		return createTestFile(inputStream, prefix);
	}

	public static Path createTestFile(InputStream inputStream, String prefix) throws IOException {
		File tempFile = File.createTempFile(prefix + "_", ".xml");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			IOUtils.copy(inputStream, fos);
		}
		log.info("Created test file " + tempFile);
		return tempFile.toPath();
	}

	public static void cleanupTestFile(Path testFile) {
		if (testFile == null) {
			return;
		}
		if (testFile.toFile().exists()) {
			if (testFile.toFile().delete()) {
				log.info("Deleted test file " + testFile);
			} else {
				log.warn("Cannot delete test file " + testFile);
			}
		}
		File loadFile = new File(testFile.getParent().toFile(), testFile.getFileName() + ".load");
		if (loadFile.exists()) {
			if (loadFile.delete()) {
				log.info("Deleted test file " + loadFile);
			} else {
				log.warn("Cannot delete test file " + loadFile);
			}
		}
	}
}
