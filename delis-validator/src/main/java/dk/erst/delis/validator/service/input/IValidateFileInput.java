package dk.erst.delis.validator.service.input;

import java.io.IOException;
import java.io.InputStream;

public interface IValidateFileInput {
	
	String getOriginalFilename();

	String getName();

	InputStream getInputStream() throws IOException;
	
}