package dk.erst.delis.web.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.JournalSendDocumentDaoRepository;
import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentProcessStepType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.task.document.parse.DocumentFormatDetectService;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.parse.data.DocumentParticipant;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.send.SendDocumentLockService;
import dk.erst.delis.task.document.storage.SendDocumentBytesStorageService;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendDocumentService {

	private SendDocumentDaoRepository sendDocumentDaoRepository;
	private JournalSendDocumentDaoRepository journalDocumentDaoRepository;

	@Autowired
	private SendDocumentLockService sendDocumentLockService;
	@Autowired
	private IdentifierResolverService identifierResolverService;
	@Autowired
	private DocumentParseService documentParseService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private SendDocumentBytesStorageService sendDocumentBytesStorageService;

	DocumentFormatDetectService documentFormatDetectService = new DocumentFormatDetectService();

	@Autowired
	private DocumentValidationTransformationService documentValidationTransformationService;

	@Autowired
	public SendDocumentService(SendDocumentDaoRepository sendDocumentDaoRepository, JournalSendDocumentDaoRepository journalSendDocumentDaoRepository) {
		this.sendDocumentDaoRepository = sendDocumentDaoRepository;
		this.journalDocumentDaoRepository = journalSendDocumentDaoRepository;
	}

	public List<SendDocument> documentList(int start, int pageSize) {
		List<SendDocument> documents = sendDocumentDaoRepository.findAll(PageRequest.of(start, pageSize, Sort.by("id").descending())).getContent();
		return documents;
	}

	public SendDocument getDocument(Long id) {
		return sendDocumentDaoRepository.findById(id).orElse(null);
	}

	public int updateStatuses(List<Long> idList, SendDocumentStatus newStatus, String user) {
		AtomicInteger count = new AtomicInteger(0);
		if (idList.size() > 0) {
			Iterable<SendDocument> documents = sendDocumentDaoRepository.findAllById(idList);
			documents.forEach(document -> {
				long start = System.currentTimeMillis();
				SendDocumentStatus oldStatus = document.getDocumentStatus();
				document.setDocumentStatus(newStatus);
				sendDocumentDaoRepository.save(document);
				noticeInJournal(oldStatus, newStatus, user, document, System.currentTimeMillis() - start);
				count.getAndIncrement();
			});
		}
		return count.get();
	}

	public int updateStatus(Long id, SendDocumentStatus newStatus, String user) {
		long start = System.currentTimeMillis();
		SendDocument document = sendDocumentDaoRepository.findById(id).get();
		if (document != null) {
			SendDocumentStatus oldStatus = document.getDocumentStatus();
			document.setDocumentStatus(newStatus);
			sendDocumentDaoRepository.save(document);
			if (user != null) {
				noticeInJournal(oldStatus, newStatus, user, document, System.currentTimeMillis() - start);
			}
			return 1;
		}
		return 0;
	}

	@Getter
	@Setter
	public static class SendDocumentData {
		private DocumentInfo documentInfo;
		private DocumentFormat documentFormat;
	}

	public SendDocument sendFile(Path path, String logMessage, boolean validate) throws Exception {
		long start = System.currentTimeMillis();
		File file = path.toFile();

		SendDocumentData sendDocumentData;
		try (InputStream is = new FileInputStream(file)) {
			sendDocumentData = processSendDocument(validate, is);
		}

		DocumentInfo documentInfo = sendDocumentData.getDocumentInfo();
		DocumentParticipant sender = documentInfo.getSender();

		Identifier identifier = identifierResolverService.resolve(sender.getSchemeId(), sender.getId());
		Organisation organisation = null;
		if (identifier != null) {
			organisation = identifier.getOrganisation();
		}

		SendDocument sd = new SendDocument();
		sd.setOrganisation(organisation);
		sd.setDocumentDate(documentInfo.getDate());
		sd.setDocumentId(documentInfo.getId());
		sd.setDocumentStatus(validate ? SendDocumentStatus.VALID : SendDocumentStatus.NEW);
		sd.setDocumentType(sendDocumentData.getDocumentFormat().getDocumentType());
		sd.setReceiverIdRaw(documentInfo.getReceiver().encodeID());
		sd.setSenderIdRaw(documentInfo.getSender().encodeID());

		sendDocumentDaoRepository.save(sd);

		long durationMs = System.currentTimeMillis() - start;
		saveJournal(sd, true, logMessage, SendDocumentProcessStepType.CREATE, durationMs);

		try (InputStream is = new FileInputStream(file)) {
			sendDocumentBytesStorageService.save(sd, SendDocumentBytesType.ORIGINAL, file.length(), is);
		}
		return sd;
	}

	protected void saveJournal(SendDocument sd, boolean success, String logMessage, SendDocumentProcessStepType stepType, long durationMs) {
		JournalSendDocument journalRecord = new JournalSendDocument();
		journalRecord.setSuccess(success);
		journalRecord.setDocument(sd);
		journalRecord.setType(stepType);
		journalRecord.setOrganisation(sd.getOrganisation());
		journalRecord.setMessage(StringUtils.truncate(logMessage, 250));
		journalRecord.setDurationMs(durationMs);
		journalDocumentDaoRepository.save(journalRecord);
	}

	public SendDocumentData processSendDocument(boolean validate, InputStream is) throws Exception, DocumentProcessStepException {
		SendDocumentData sendDocumentData = new SendDocumentData();

		sendDocumentData.setDocumentInfo(documentParseService.parseDocumentInfo(is));
		DocumentInfo documentInfo = sendDocumentData.getDocumentInfo();

		sendDocumentData.setDocumentFormat(documentFormatDetectService.defineDocumentFormat(documentInfo));
		DocumentFormat documentFormat = sendDocumentData.getDocumentFormat();

		if (documentFormat == DocumentFormat.UNSUPPORTED) {
			throw new Exception("Document format is unsupported: " + documentInfo.getRoot() + ", " + documentInfo.getProfile() + ", " + documentInfo.getCustomizationID());
		}

		if (validate) {
			is.reset();
			validate(is, documentFormat);
		}
		return sendDocumentData;
	}

	protected void validate(InputStream xmlInputStream, DocumentFormat documentFormat) throws DocumentProcessStepException {
		List<RuleDocumentValidation> ruleByFormat = ruleService.getValidationRuleListByFormat(documentFormat);
		for (RuleDocumentValidation ruleDocumentValidation : ruleByFormat) {
			try {
				xmlInputStream.reset();
				DocumentProcessStep step = documentValidationTransformationService.validateByRule(xmlInputStream, ruleDocumentValidation);
				if (!step.isSuccess()) {
					StringBuilder sb = new StringBuilder();
					sb.append(step.getErrorCode());
					sb.append(" failed with ");
					if (step.getErrorRecords() != null) {
						List<ErrorRecord> errorRecords = step.getErrorRecords();
						sb.append(errorRecords.size() + " errors: ");
						for (ErrorRecord errorRecord : errorRecords) {
							sb.append(errorRecord.toString());
						}
					} else {
						sb.append(step.getMessage());
					}
					throw new DocumentProcessStepException(null, "Document is resolved as " + documentFormat + " but is not valid: " + step.getMessage(), step);
				}
			} catch (IOException e) {
				log.error("Failed to validate document by rule " + ruleDocumentValidation, e);
				DocumentProcessStep step = new DocumentProcessStep(ruleDocumentValidation);
				ErrorRecord err = new ErrorRecord(ruleDocumentValidation.buildErrorCode(), "", e.getMessage(), "error", "IOException");
				step.addError(err);
				step.setMessage(e.getMessage());
				throw new DocumentProcessStepException(null, "Document is resolved as " + documentFormat + " but is not valid: " + step.getMessage(), step);
			}
		}
	}

	private void noticeInJournal(SendDocumentStatus oldStatus, SendDocumentStatus newStatus, String user, SendDocument document, long duration) {
		JournalSendDocument updateRecord = new JournalSendDocument();
		updateRecord.setDocument(document);
		updateRecord.setSuccess(true);
		updateRecord.setType(SendDocumentProcessStepType.MANUAL);
		updateRecord.setOrganisation(document.getOrganisation());
		updateRecord.setMessage(StringUtils.truncate(MessageFormat.format("User {0} manually changed status from {1} to {2}", user, oldStatus, newStatus), 250));
		updateRecord.setDurationMs(duration);
		journalDocumentDaoRepository.save(updateRecord);
	}

	public List<JournalSendDocument> getDocumentRecords(SendDocument document) {
		return journalDocumentDaoRepository.findByDocumentOrderByIdAsc(document);
	}

	public List<SendDocumentBytes> getDocumentBytes(SendDocument document) {
		return this.sendDocumentBytesStorageService.findAll(document);
	}

	public void getDocumentBytesContents(SendDocumentBytes sdb, OutputStream out) {
		this.sendDocumentBytesStorageService.load(sdb, out);
	}

	public SendDocumentBytes findDocumentBytes(long documentId, long bytesId) {
		return this.sendDocumentBytesStorageService.find(documentId, bytesId);
	}

	public StatData validateNewDocuments() {
		StatData statData = new StatData();
		SendDocument sendDocument;

		while ((sendDocument = sendDocumentLockService.findDocumentAndLock(SendDocumentStatus.NEW, SendDocumentStatus.VALIDATE_START)) != null) {
			validateNewDocument(sendDocument, statData);
		}
		
		log.info("Done validation of new documents: "+statData);
		return statData;
	}

	public void validateNewDocument(SendDocument sendDocument, StatData statData) {
		SendDocumentStatus resultStatus = null;
		String logMessage = "Unexpected error";
		long start = System.currentTimeMillis();
		try {
			SendDocumentBytes documentBytes = sendDocumentBytesStorageService.find(sendDocument, SendDocumentBytesType.ORIGINAL);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (!sendDocumentBytesStorageService.load(documentBytes, baos)) {
				throw new Exception("Failed to load SendDocumentBytes by " + documentBytes);
			}
			processSendDocument(true, new ByteArrayInputStream(baos.toByteArray()));
			resultStatus = SendDocumentStatus.VALID;
			logMessage = "Document is valid";
		} catch (DocumentProcessStepException e) {
			log.error("Document is not valid: " + sendDocument, e);
			logMessage = "Document is not valid: " + e.getMessage();
		} catch (Throwable e) {
			log.error("Failed to validate SendDocument " + sendDocument, e);
			logMessage = "Document failed validation with error " + e.getMessage();
		} finally {
			if (resultStatus == null) {
				resultStatus = SendDocumentStatus.VALIDATE_ERROR;
			}
			sendDocumentLockService.unlock(sendDocument, SendDocumentStatus.VALIDATE_START, resultStatus);
			saveJournal(sendDocument, resultStatus == SendDocumentStatus.VALID, logMessage, SendDocumentProcessStepType.VALIDATE, System.currentTimeMillis() - start);
			statData.increment(resultStatus.toString());
		}
	}
}
