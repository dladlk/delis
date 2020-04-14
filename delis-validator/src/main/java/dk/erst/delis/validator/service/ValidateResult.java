package dk.erst.delis.validator.service;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import lombok.Data;

@Data
public class ValidateResult {

	private String fileName;
	private long fileSize;
	private DocumentFormat documentFormat;
	private boolean documentFormatDetected;
	private DocumentProcessLog processLog;

}