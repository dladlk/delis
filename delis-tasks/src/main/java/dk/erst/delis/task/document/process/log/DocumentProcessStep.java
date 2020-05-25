package dk.erst.delis.task.document.process.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class DocumentProcessStep {

	private long start;
	private long duration;
	private boolean success;

	private String description;
	private String message;
	private Object result;
	private final DocumentProcessStepType stepType;
	
	private DocumentErrorCode errorCode;

	private List<ErrorRecord> errorRecords = new ArrayList<>();
	
	private int countError;
	private int countWarning;

	public DocumentProcessStep(String description, DocumentProcessStepType stepType) {
		this.description = description;
		this.stepType = stepType;
		this.start = System.currentTimeMillis();

		log.info(description);
	}

	public DocumentProcessStep(RuleDocumentValidation rule) {
		this("Validate with " + rule.getRootPath(), rule.getValidationType().isXSD() ? DocumentProcessStepType.VALIDATE_XSD : DocumentProcessStepType.VALIDATE_SCH);
		this.errorCode = rule.buildErrorCode();
	}

	public DocumentProcessStep(RuleDocumentTransformation rule) {
		this("Transform with " + rule.getRootPath(), DocumentProcessStepType.TRANSFORM);
	}

	public void done() {
		if (this.errorRecords != null) {
			int error = 0;
			int warn = 0;
			for (ErrorRecord errorRecord : errorRecords) {
				if (errorRecord.isWarning()) {
					warn++;
				} else {
					error++;
				}
			}
			this.countError = error;
			this.countWarning = warn;
		}
		this.duration = System.currentTimeMillis() - start;

		log.info("Done " + (success ? "OK" : "ERROR") + " in " + duration);
	}
	
	public void setResult(Object result) {
		this.result = result;
	}

	public Date getStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(this.start);
		return c.getTime();
	}

	public List<ErrorRecord> getErrorRecords() {
		return errorRecords;
	}

	public void addError (ErrorRecord error) {
		errorRecords.add(error);
	}
	
	public static DocumentProcessStep buildDefineFormatStep() {
		return new DocumentProcessStep("Define format", DocumentProcessStepType.RESOLVE_TYPE);
	}

	public void fillDefineFormatError(DocumentInfo info) {
		String description = info != null ? info.toFormatDescription() : "";
		String location = null;
		if (info != null && info.getRoot() != null) {
			if (StringUtils.isNotBlank(info.getRoot().getRootTag())) {
				location = "/" + info.getRoot().getRootTag();
			}
		}
		ErrorRecord error = new ErrorRecord(DocumentErrorCode.OTHER, "Format", "Unsupported format: " + description, "fatal", location);
		this.addError(error);
		this.setSuccess(false);
		this.done();		
	}
}
