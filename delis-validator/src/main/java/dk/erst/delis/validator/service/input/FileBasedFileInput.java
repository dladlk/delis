package dk.erst.delis.validator.service.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileBasedFileInput implements IValidateFileInput {

	private File file;

	public FileBasedFileInput(File file) {
		this.file = file;
	}

	@Override
	public String getOriginalFilename() {
		return file.getName();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

}
