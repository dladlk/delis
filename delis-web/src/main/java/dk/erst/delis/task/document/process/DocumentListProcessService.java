package dk.erst.delis.task.document.process;

import java.nio.file.Paths;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.DocumentRepository;
import dk.erst.delis.dao.JournalDocumentRepository;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.JournalDocument;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentListProcessService {

	private DocumentProcessService documentProcessService;

	private DocumentRepository documentRepository;

	private JournalDocumentRepository journalDocumentRepository;

	private ConfigBean configBean;

	@Autowired
	public DocumentListProcessService(ConfigBean configBean, DocumentProcessService documentProcessService, DocumentRepository documentRepository,
			JournalDocumentRepository journalDocumentRepository) {
		this.configBean = configBean;
		this.documentProcessService = documentProcessService;
		this.documentRepository = documentRepository;
		this.journalDocumentRepository = journalDocumentRepository;
	}

	public StatData processLoaded() {
		boolean presentLoaded = false;

		StatData statData = new StatData();
		try {
			do {
				List<Document> list = documentRepository.findTop5ByDocumentStatusOrderByIdAsc(DocumentStatus.LOAD_OK);
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

	private void processDocument(StatData statData, Document document) {
		document.setDocumentStatus(DocumentStatus.VALIDATE_START);
		documentRepository.updateDocumentStatus(document);

		DocumentProcessLog log = documentProcessService.process(document, Paths.get(configBean.getStorageLoadedPath().toString(), document.getIngoingRelativePath()));
		if (log != null) {
			statData.increment(log.isSuccess() ? "OK" : "ERROR");

			document.setDocumentStatus(log.isSuccess() ? DocumentStatus.VALIDATE_OK : DocumentStatus.VALIDATE_ERROR);

			documentRepository.updateDocumentStatus(document);

			List<DocumentProcessStep> stepList = log.getStepList();
			if (stepList != null && !stepList.isEmpty()) {
				for (DocumentProcessStep step : stepList) {
					JournalDocument j = new JournalDocument();
					j.setCreateTime(Date.from(Instant.ofEpochMilli(step.getStart())));
					j.setDurationMs(step.getDuration());
					j.setDocument(document);
					j.setOrganisation(document.getOrganisation());
					j.setMessage(buildJournalMessage(step));
					journalDocumentRepository.save(j);
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
