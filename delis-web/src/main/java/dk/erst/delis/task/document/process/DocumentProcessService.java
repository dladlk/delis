package dk.erst.delis.task.document.process;

import java.nio.file.Path;
import java.util.List;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.enums.document.DocumentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentProcessService {

	private DocumentValidationTransformationService documentValidationTransformationService;

	private DocumentDaoRepository documentDaoRepository;

	private JournalDocumentDaoRepository journalDocumentDaoRepository;

	private DocumentBytesStorageService documentBytesStorageService;
	
	@Autowired
	public DocumentProcessService(DocumentBytesStorageService documentBytesStorageService, DocumentValidationTransformationService documentValidationTransformationService, DocumentDaoRepository documentDaoRepository,
			JournalDocumentDaoRepository journalDocumentDaoRepository) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.documentValidationTransformationService = documentValidationTransformationService;
		this.documentDaoRepository = documentDaoRepository;
		this.journalDocumentDaoRepository = journalDocumentDaoRepository;
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
		
		Path ingoingFormatPath = documentBytesStorageService.getIngoingFormatPath(document);

		DocumentProcessLog log = documentValidationTransformationService.process(document, ingoingFormatPath);
		if (log != null) {
			statData.increment(log.isSuccess() ? "OK" : "ERROR");

			String outgoingRelativePath = null;
			if (log.isSuccess()) {
				outgoingRelativePath = documentBytesStorageService.moveToValid(document, log.getResultPath());
			} else {
				outgoingRelativePath = documentBytesStorageService.moveToFailed(document, log.getResultPath());
			}
			document.setOutgoingRelativePath(outgoingRelativePath);
			
			documentDaoRepository.updateOutgoingRelativePath(document);
			
			document.setDocumentStatus(log.isSuccess() ? DocumentStatus.VALIDATE_OK : DocumentStatus.VALIDATE_ERROR);

			documentDaoRepository.updateDocumentStatus(document);

			List<DocumentProcessStep> stepList = log.getStepList();
			if (stepList != null && !stepList.isEmpty()) {
				for (DocumentProcessStep step : stepList) {
					JournalDocument j = new JournalDocument();
					j.setSuccess(step.isSuccess());
					j.setType(step.getStepType());
					j.setCreateTime(step.getStartTime());
					j.setDurationMs(step.getDuration());
					j.setDocument(document);
					j.setOrganisation(document.getOrganisation());
					j.setMessage(buildJournalMessage(step));
					journalDocumentDaoRepository.save(j);
				}
			}

		} else {
			statData.increment("UNDEFINED");
		}
	}

	private String buildJournalMessage(DocumentProcessStep step) {
		if (step != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(step.getDescription());
			if (step.getMessage() != null) {
				sb.append(": ");
				sb.append(step.getMessage());
			}
			String message = sb.toString();
			if (message.length() > 255) {
				return message.substring(0, 250)+"...";
			}
			return message;
		}
		return "";
	}

}
