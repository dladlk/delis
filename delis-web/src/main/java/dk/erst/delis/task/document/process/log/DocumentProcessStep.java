package dk.erst.delis.task.document.process.log;

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

	public DocumentProcessStep(String description) {
		this.description = description;
		this.start = System.currentTimeMillis();

		log.info(description);
	}

	public DocumentProcessStep(RuleDocumentValidation rule) {
		this("Validate with " + rule.getRootPath());
	}

	public DocumentProcessStep(RuleDocumentTransformation rule) {
		this("Transform with " + rule.getRootPath());
	}

	public void done() {
		this.duration = System.currentTimeMillis() - start;

		log.info("Done " + (success ? "OK" : "ERROR") + " in " + duration);
	}
}
