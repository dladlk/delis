package dk.erst.delis.task.document.storage;

import dk.erst.delis.data.entities.document.DocumentBytes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class DocumentBytesStorageService {

	@Autowired
	public DocumentBytesStorageService(){
	}

	public boolean save (DocumentBytes documentBytes, InputStream stream) {
		boolean result = true;
		String location = documentBytes.getLocation();
		Path path = Paths.get(location);
		try {
			Files.copy(stream, path);
		} catch (IOException e) {
			log.error("Failed to save document bytes to " + path, e);
			result = false;
		}
		return result;
	}

	public boolean load (DocumentBytes documentBytes, OutputStream stream) {
		boolean result = true;
		String location = documentBytes.getLocation();
		Path path = Paths.get(location);
		try {
			byte[] bytes = Files.readAllBytes(path);
			stream.write(bytes);
		} catch (IOException e) {
			log.error("Failed to read document bytes from " + path, e);
			result = false;
		}
		return result;
	}

	public boolean copy (DocumentBytes from, DocumentBytes to) {
		boolean result = true;
		Path pathFrom = Paths.get(from.getLocation());
		Path pathTo = Paths.get(to.getLocation());
		try {
			Files.copy(pathFrom, pathTo);
		} catch (IOException e) {
			log.error("Failed to read document bytes from " + pathFrom + " to " + pathTo, e);
			result = false;
		}
		return result;
	}
}
