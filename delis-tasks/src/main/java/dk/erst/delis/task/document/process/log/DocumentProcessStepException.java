package dk.erst.delis.task.document.process.log;

import lombok.Getter;

@Getter
public class DocumentProcessStepException extends Exception {

	private static final long serialVersionUID = 6623913831322756847L;
	private DocumentProcessStep step;
	private Long documentId;

	public DocumentProcessStepException(Long documentId, String message, DocumentProcessStep step) {
		super(message);
		this.documentId = documentId;
		this.step = step;
	}
}
