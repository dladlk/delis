package dk.erst.delis.task.document.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.response.ApplicationResponseService;
import dk.erst.delis.task.document.response.ApplicationResponseService.ApplicationResponseGenerationException;
import dk.erst.delis.task.document.response.ApplicationResponseService.MessageLevelResponseGenerationData;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentProcessService {

	private DocumentValidationTransformationService documentValidationTransformationService;

	private DocumentDaoRepository documentDaoRepository;

	private JournalDocumentService journalDocumentService;

	private DocumentBytesStorageService documentBytesStorageService;
	
	private OrganisationSetupService organisationSetupService;
	
	private ApplicationResponseService invoiceResponseService;
	
	private SendDocumentService sendDocumentService;

	@Autowired
	public DocumentProcessService(DocumentBytesStorageService documentBytesStorageService,
								  DocumentValidationTransformationService documentValidationTransformationService,
								  DocumentDaoRepository documentDaoRepository,
								  JournalDocumentService journalDocumentService,
								  OrganisationSetupService organisationSetupService, 
								  ApplicationResponseService invoiceResponseService,
								  SendDocumentService sendDocumentService) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.documentValidationTransformationService = documentValidationTransformationService;
		this.documentDaoRepository = documentDaoRepository;
		this.journalDocumentService = journalDocumentService;
		this.organisationSetupService = organisationSetupService;
		this.invoiceResponseService = invoiceResponseService;
		this.sendDocumentService = sendDocumentService;
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
		Organisation organisation = document.getOrganisation();
		if (organisation == null) {
			log.warn("Document has no assigned organisation, skip it: "+document);
			statData.increment("UNDEFINED");
			return;
		}
		OrganisationReceivingFormatRule receivingFormatRule = OrganisationReceivingFormatRule.getDefault();
		OrganisationSetupData setupData = organisationSetupService.load(organisation);
		if (setupData == null || setupData.getReceivingFormatRule() == null) {
			log.warn("Organisation "+organisation+" has no setup - use default value for receving format "+receivingFormatRule);
		} else {
			receivingFormatRule = setupData.getReceivingFormatRule();
			log.info("Receiving format rule for "+organisation+" is set to "+receivingFormatRule);
		}
		
		document.setDocumentStatus(DocumentStatus.VALIDATE_START);
		documentDaoRepository.updateDocumentStatus(document);

		DocumentBytes documentBytesLoaded = documentBytesStorageService.find(document, DocumentBytesType.IN);
		if (documentBytesLoaded == null) {
			statData.increment("Document xml not found");
			return;
		}

		Path xmlLoadedPath;
		String prefix = "process_"+document.getId() + "_";
		try {
			xmlLoadedPath = Files.createTempFile(prefix, ".xml");
		} catch (IOException e) {
			log.error("Failed to create temp file with prefix "+prefix, e);
			return;
		}
		try (OutputStream fos = Files.newOutputStream(xmlLoadedPath)) {
			documentBytesStorageService.load(documentBytesLoaded, fos);
		} catch (Exception e) {
			log.error("Failed to read file from storage by "+documentBytesLoaded, e);
		}

		
		DocumentProcessLog plog = documentValidationTransformationService.process(document, xmlLoadedPath, receivingFormatRule);
		DocumentErrorCode lastError = null;
		if (plog != null) {
			statData.increment(plog.isSuccess() ? "OK" : "ERROR");

			if (plog.isSuccess()) {
				File file = plog.getResultPath().toFile();
				try {
					documentBytesStorageService.save(document, DocumentBytesType.READY, file.length(), Files.newInputStream(file.toPath()));
				} catch (IOException e) {
					String description = "Can not save validated document " + document.getName();
					log.error(description, e);
					DocumentProcessStep step = new DocumentProcessStep(description, DocumentProcessStepType.COPY);
					step.setMessage(e.getMessage());
					step.setSuccess(false);
					plog.addStep(step);
				}
			} else {
				DocumentProcessStep lastStep = null;
				if (plog.getStepList() != null && !plog.getStepList().isEmpty()) {
					lastStep = plog.getStepList().get(plog.getStepList().size() - 1);
				}
				if (lastStep != null) {
					lastError = lastStep.getErrorCode();
				} else {
					lastError = DocumentErrorCode.OTHER;
				}
			}

			document.setDocumentStatus(plog.isSuccess() ? DocumentStatus.VALIDATE_OK : DocumentStatus.VALIDATE_ERROR);
			document.setLastError(lastError);

			documentDaoRepository.updateDocumentStatus(document);

			List<DocumentProcessStep> stepList = plog.getStepList();
			journalDocumentService.saveDocumentStep(document, stepList);
			
			if (!plog.isSuccess()) {
				if (setupData.isGenerateInvoiceResponseOnError()) {
					DocumentProcessStep lastFailedStep = lastLastFailedStep(stepList);
					if (generateAndSendMessageLevelResponse(document, lastFailedStep).isSuccess()) {
						statData.increment("GENERATED_MLR_OK");
					} else {
						statData.increment("GENERATED_MLR_ERROR");
					}
				}
			}
		} else {
			statData.increment("UNDEFINED");
		}
	}

	public DocumentProcessStep lastLastFailedStep(List<DocumentProcessStep> stepList) {
		DocumentProcessStep lastFailedStep = null;
		if (stepList != null && !stepList.isEmpty()) {
			for (int i = stepList.size() - 1; i >= 0; i--) {
				DocumentProcessStep step = stepList.get(i);
				if (!step.isSuccess()) {
					lastFailedStep = step;
					break;
				}
			}
		}
		return lastFailedStep;
	}

	public DocumentProcessStep generateAndSendMessageLevelResponse(Document document, DocumentProcessStep lastFailedStep) {
		DocumentProcessStep step = new DocumentProcessStep("Generate and send MessageLevelResponse", DocumentProcessStepType.GENERATE_RESPONSE);
		SendDocument sendDocument = null;
		String errorMessage = null;
		DocumentProcessStep failedStep = null;
		
		try {
			sendDocument = generateAndSendMessageLevelResponseInternal(document, lastFailedStep);
		} catch (ApplicationResponseGenerationException e) {
			log.error("Failed MessageLevelResponse generation", e);
			errorMessage = e.getMessage();
			failedStep = e.getFailedStep();
		} finally {
			step.done();
			List<DocumentProcessStep> irStepList = new ArrayList<>();
			
			String appendMessage;
			if (sendDocument == null) {
				step.setSuccess(false);
				appendMessage = " failed: " + errorMessage;
			} else {
				step.setSuccess(true);
				appendMessage = " #"+sendDocument.getDocumentId();
			}
			step.setMessage((step.getMessage() != null ? step.getMessage() : "") + appendMessage);
			step.setResult(sendDocument);
			
			if (failedStep != null) {
				step.setErrorRecords(failedStep.getErrorRecords());
			}
			
			irStepList.add(step);
			
			journalDocumentService.saveDocumentStep(document, irStepList);
		}
		return step;
	}
	
	private SendDocument generateAndSendMessageLevelResponseInternal(Document document, DocumentProcessStep lastFailedStep) throws ApplicationResponseGenerationException {
		Path tempPath;
		try {
			tempPath = Files.createTempFile("mlr_"+document.getId()+"_", ".xml");
		} catch (IOException e) {
			throw new ApplicationResponseGenerationException(document.getId(), "Failed to create temp file", e);
		}

		try {
			MessageLevelResponseGenerationData d = invoiceResponseService.buildMLRDataByFailedStep(lastFailedStep);
			try (OutputStream out = new FileOutputStream(tempPath.toFile())) {
				invoiceResponseService.generateApplicationResponse(document, d, out);
			}
		} catch (ApplicationResponseGenerationException e) {
			throw e;
		} catch (Exception e) {
			throw new ApplicationResponseGenerationException(document.getId(), "Failed to store generated invoice to temp file", e);
		}
		try {
			return sendDocumentService.sendFile(tempPath, "Generated by last error on document "+document.getId(),true);
		} catch (DocumentProcessStepException se) {
			throw new ApplicationResponseGenerationException(document.getId(), se.getMessage(), se.getStep(), se);
		} catch (Exception e) {
			throw new ApplicationResponseGenerationException(document.getId(), e.getMessage(), e);
		}
	}

}