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
				if (setupData.getReceivingMethod() == null) {
					log.info("No recieving method defined for organization " + org.getName() + ". Documents delivery skipped");
					statData.increment("Organizations with no recieving method");
				} else {
					boolean presentValidated;
					Long lastFailedInCurrentProcessing = 0l;
					do {
						List<Document> list = documentDaoRepository.findForExport(DocumentStatus.VALIDATE_OK, org, lastFailedInCurrentProcessing);
						presentValidated = !list.isEmpty();

						for (Document document : list) {
							if (!exportDocument(statData, document, setupData)) {
								lastFailedInCurrentProcessing = document.getId();
							}
						}

					} while (presentValidated);
				}
			}
		} finally {
			log.info("Done exporting of validated documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
		}

		return statData;
	}

	public boolean exportDocument(StatData statData, Document document, OrganisationSetupData setupData) {
		document.setDocumentStatus(DocumentStatus.EXPORT_START);
		documentDaoRepository.updateDocumentStatus(document);

		DocumentProcessLog log = moveDocument(document, setupData);

		document.setDocumentStatus(log.isSuccess() ? DocumentStatus.EXPORT_OK : DocumentStatus.VALIDATE_OK);
		documentDaoRepository.updateDocumentStatus(document);


		if (log != null) {
			statData.increment(log.isSuccess() ? "OK" : "ERROR");
			List<DocumentProcessStep> stepList = log.getStepList();
			journalDocumentService.saveDocumentStep(document, stepList);
		} else {
			statData.increment("UNDEFINED");
		}

		return log.isSuccess();
	}

	private DocumentProcessLog moveDocument(Document document, OrganisationSetupData setupData) {
		DocumentProcessLog log = new DocumentProcessLog();

		OrganisationReceivingMethod receivingMethod = setupData.getReceivingMethod();
		String receivingMethodSetup = setupData.getReceivingMethodSetup();

		if (receivingMethod == null) {
			DocumentProcessStep failStep = new DocumentProcessStep("Can not export - receiving method is not set for organization " + document.getOrganisation().getName(), DocumentProcessStepType.DELIVER);
			failStep.setSuccess(false);
			failStep.done();
			log.addStep(failStep);
		} else {
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
