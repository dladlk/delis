package dk.erst.delis.task.document.deliver;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DocumentDeliverService {

	private DocumentDaoRepository documentDaoRepository;
	private OrganisationSetupService organisationSetupService;
	private JournalDocumentService journalDocumentService;
	private DocumentBytesStorageService documentBytesStorageService;


	@Autowired
	public DocumentDeliverService(DocumentDaoRepository documentDaoRepository, OrganisationSetupService organisationSetupService,
								  JournalDocumentService journalDocumentService, DocumentBytesStorageService documentBytesStorageService) {
		this.documentDaoRepository = documentDaoRepository;
		this.organisationSetupService = organisationSetupService;
		this.journalDocumentService = journalDocumentService;
		this.documentBytesStorageService = documentBytesStorageService;
	}

	public StatData processValidated() {

		StatData statData = new StatData();
		try {
			List<Organisation> organisations = documentDaoRepository.loadDocumentStatusStat(DocumentStatus.VALIDATE_OK);
			for (Organisation org : organisations) {
				OrganisationSetupData setupData = organisationSetupService.load(org);
				boolean presentValidated;
				do {
					List<Document> list = documentDaoRepository.findTop5ByDocumentStatusAndOrganisationOrderByIdAsc(DocumentStatus.VALIDATE_OK, org);
					presentValidated = !list.isEmpty();

					for (Document document : list) {
						exportDocument(statData, document, setupData);
					}

				} while (presentValidated);
			}
		} finally {
			log.info("Done exporting of validated documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
		}

		return statData;
	}

	public void exportDocument(StatData statData, Document document, OrganisationSetupData setupData) {
		document.setDocumentStatus(DocumentStatus.EXPORT_START);
		documentDaoRepository.updateDocumentStatus(document);

		DocumentProcessLog log = moveDocument(document, setupData);

		document.setDocumentStatus(DocumentStatus.EXPORT_OK);
		documentDaoRepository.updateDocumentStatus(document);


		if (log != null) {
			statData.increment(log.isSuccess() ? "OK" : "ERROR");
			List<DocumentProcessStep> stepList = log.getStepList();
			journalDocumentService.saveDocumentStep(document, stepList);
		} else {
			statData.increment("UNDEFINED");
		}
	}

	private DocumentProcessLog moveDocument(Document document, OrganisationSetupData setupData) {
		DocumentProcessLog log = new DocumentProcessLog();

		OrganisationReceivingMethod receivingMethod = setupData.getReceivingMethod();
		String receivingMethodSetup = setupData.getReceivingMethodSetup();

		switch (receivingMethod) {
			case FILE_SYSTEM:
				moveToFileSystem(document, receivingMethodSetup, log);
				break;
			case AZURE_STORAGE_ACCOUNT:
				moveToAzure(document, receivingMethodSetup, log);
				break;
			default:
				DocumentProcessStep failStep = new DocumentProcessStep("Can not export - can not recognize receiving method " + receivingMethod, DocumentProcessStepType.DELIVER);
				failStep.setSuccess(false);
				failStep.done();
				log.addStep(failStep);
		}

		return log;
	}



	private void moveToFileSystem(Document document, String path, DocumentProcessLog log) {
		DocumentProcessStep step = new DocumentProcessStep("Export to " + path, DocumentProcessStepType.DELIVER);

		String deliver = documentBytesStorageService.moveToDeliver(document, path);
		step.setSuccess(deliver != null);
		step.done();
		log.addStep(step);
	}

	private void moveToAzure(Document document, String path, DocumentProcessLog log) {
		DocumentProcessStep failStep = new DocumentProcessStep("Delivering to Azure storage not implemented yet", DocumentProcessStepType.DELIVER);
		failStep.setSuccess(false);
		failStep.done();
		log.addStep(failStep);
	}
}
