package dk.erst.delis.validator.service.input;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileInput implements IValidateFileInput {

	private MultipartFile file;

	public MultipartFileInput(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String getOriginalFilename() {
		return file.getOriginalFilename();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}

}