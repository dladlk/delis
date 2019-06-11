package dk.erst.delis.task.document.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransformationResultListener {

	private DocumentBytesStorageService documentBytesStorageService;
	private Document document;

	public TransformationResultListener(DocumentBytesStorageService documentBytesStorageService, Document document) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.document = document;
	}

	public void notify(DocumentProcessLog plog, DocumentFormat resultFormat, File file) {
		try {
			documentBytesStorageService.save(document, DocumentBytesType.INTERM, resultFormat, file.length(), Files.newInputStream(file.toPath()));
		} catch (IOException e) {
			String description = "Can not save validated document " + document.getName();
			log.error(description, e);
			DocumentProcessStep step = new DocumentProcessStep(description, DocumentProcessStepType.COPY);
			step.setMessage(e.getMessage());
			step.setSuccess(false);
			plog.addStep(step);
		}
		
	}

}
