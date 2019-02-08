package dk.erst.delis.task.document.process;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.DocumentBytesService;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class DocumentProcessService {

	private DocumentValidationTransformationService documentValidationTransformationService;

	private DocumentDaoRepository documentDaoRepository;

	private JournalDocumentService journalDocumentService;

	private DocumentBytesStorageService documentBytesStorageService;

	private DocumentBytesService documentBytesService;

	private ConfigBean configBean;
	
	@Autowired
	public DocumentProcessService(DocumentBytesStorageService documentBytesStorageService,
								  DocumentValidationTransformationService documentValidationTransformationService,
								  DocumentDaoRepository documentDaoRepository,
								  JournalDocumentService journalDocumentService,
								  DocumentBytesService documentBytesService,
								  ConfigBean configBean) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.documentValidationTransformationService = documentValidationTransformationService;
		this.documentDaoRepository = documentDaoRepository;
		this.journalDocumentService = journalDocumentService;
		this.documentBytesService = documentBytesService;
		this.configBean = configBean;
	}

	public StatData processLoaded() {
		boolean presentLoaded = false;

		StatData statData = new StatData();
		try {
			do {
				List<Document> list = documentDaoRepository.findTop5ByDocumentStatusOrderByIdAsc(DocumentStatus.LOAD_OK);
				presentLoaded = !list.isEmpty();

				for (Document document : list) {
					processDocument(statData, document);
				}

			} while (presentLoaded);
		} finally {
			log.info("Done processing of loaded documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
		}

		return statData;
	}

	public void processDocument(StatData statData, Document document) {
		document.setDocumentStatus(DocumentStatus.VALIDATE_START);
		documentDaoRepository.updateDocumentStatus(document);

		DocumentBytes documentBytesLoaded = documentBytesService.findDocumentBytesLoaded(document);

		DocumentProcessLog plog = documentValidationTransformationService.process(document, documentBytesLoaded);
		if (plog != null) {
			statData.increment(plog.isSuccess() ? "OK" : "ERROR");

			if (plog.isSuccess()) {
				Path destRoot = configBean.getStorageValidPath();
				Path destSubPath = Paths.get(document.getName()).getFileName();
				Path path = destRoot.resolve(destSubPath);

				DocumentBytes readyDocumentBytes = documentBytesService.createReadyDocumentBytes(document, path.toAbsolutePath().toString());
				try {
					documentBytesStorageService.save(readyDocumentBytes, Files.newInputStream(plog.getResultPath()));
					documentBytesService.saveDocumentBytes(readyDocumentBytes);
				} catch (IOException e) {
					String description = "Can not save validated document " + document.getName();
					log.error(description, e);
					DocumentProcessStep step = new DocumentProcessStep(description, DocumentProcessStepType.COPY);
					step.setMessage(e.getMessage());
					step.setSuccess(false);
					plog.addStep(step);
				}
//			} else {
//				DocumentBytes readyDocumentBytes = documentBytesService.createReadyDocumentBytes(document);
//				outgoingRelativePath = documentBytesStorageService.moveToFailed(document, log.getResultPath());
			}

			document.setDocumentStatus(plog.isSuccess() ? DocumentStatus.VALIDATE_OK : DocumentStatus.VALIDATE_ERROR);

			documentDaoRepository.updateDocumentStatus(document);

			List<DocumentProcessStep> stepList = plog.getStepList();
			journalDocumentService.saveDocumentStep(document, stepList);
		} else {
			statData.increment("UNDEFINED");
		}
	}
}
