package dk.erst.delis.task.document.process.log;

import java.util.Calendar;
import java.util.Date;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.RuleDocumentTransformation;
import dk.erst.delis.data.RuleDocumentValidation;
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
	private DocumentErrorCode errorCode;
	private Object result;
	private final DocumentProcessStepType stepType;

	public DocumentProcessStep(String description, DocumentProcessStepType stepType) {
		this.description = description;
		this.stepType = stepType;
		this.start = System.currentTimeMillis();

		log.info(description);
	}

	public DocumentProcessStep(RuleDocumentValidation rule) {
		this("Validate with " + rule.getRootPath(), rule.getValidationType().isXSD() ? DocumentProcessStepType.VALIDATE_XSD : DocumentProcessStepType.VALIDATE_SCH);
	}

	public DocumentProcessStep(RuleDocumentTransformation rule) {
		this("Transform with " + rule.getRootPath(), DocumentProcessStepType.TRANSFORM);
	}

	public void done() {
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
}
