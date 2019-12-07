package dk.erst.delis.task.document.deliver;

import dk.erst.delis.data.entities.document.Document;

public class UploadFailureException extends Exception {

	private static final long serialVersionUID = -4260235070912053975L;

	public UploadFailureException(String reason, Document document, String outputFile) {
		super("Failed to upload document for " + document.getOrganisation().getName() + " with reason:" + reason);
	}
}
