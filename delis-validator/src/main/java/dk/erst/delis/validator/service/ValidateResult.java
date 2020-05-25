package dk.erst.delis.validator.service;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = { "processLog" })
public class ValidateResult {

	private long start;
	private long duration;
	private String fileName;
	private long fileSize;
	private DocumentFormat documentFormat;
	private boolean documentFormatDetected;
	private ValidateRestResult restResult;
	private DocumentProcessLog processLog;

	public ValidateResult() {
		this.start = System.currentTimeMillis();
	}

	public boolean isSuccess() {
		return this.processLog != null && this.processLog.isSuccess();
	}

	public void setRestResult(ValidateRestResult restResult) {
		this.restResult = restResult;
		this.duration = System.currentTimeMillis() - this.start;
	}

}