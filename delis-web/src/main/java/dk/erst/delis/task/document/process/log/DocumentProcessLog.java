package dk.erst.delis.task.document.process.log;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DocumentProcessLog {

	private List<DocumentProcessStep> stepList = new ArrayList<>();
	
	private Path resultPath;
	
	private boolean success;

	public DocumentProcessLog() {
		this.stepList = new ArrayList<>();
		this.success = true;
	}
	
	public void addStep(DocumentProcessStep step) {
		this.stepList.add(step);
		if (!step.isSuccess()) {
			this.success = false;
		}
	}
}
