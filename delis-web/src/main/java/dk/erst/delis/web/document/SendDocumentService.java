package dk.erst.delis.web.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

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
import dk.erst.delis.pagefiltering.response.PageContainer;
import dk.erst.delis.pagefiltering.service.AbstractGenerateDataService;
import dk.erst.delis.pagefiltering.service.AbstractService;
import dk.erst.delis.task.document.parse.DocumentFormatDetectService;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.parse.data.DocumentParticipant;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.SendDocumentBytesStorageService;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;

@Service
public class SendDocumentService implements AbstractService<SendDocument> {
	private SendDocumentDaoRepository documentDaoRepository;
	private JournalSendDocumentDaoRepository journalDocumentDaoRepository;
	private final AbstractGenerateDataService<SendDocumentDaoRepository, SendDocument> abstractGenerateDataService;

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
	public SendDocumentService(SendDocumentDaoRepository sendDocumentDaoRepository, JournalSendDocumentDaoRepository journalSendDocumentDaoRepository, AbstractGenerateDataService<SendDocumentDaoRepository, SendDocument> abstractGenerateDataService) {
		this.documentDaoRepository = sendDocumentDaoRepository;
		this.journalDocumentDaoRepository = journalSendDocumentDaoRepository;
		this.abstractGenerateDataService = abstractGenerateDataService;
	}

	public List<SendDocument> documentList(int start, int pageSize) {
		List<SendDocument> documents = documentDaoRepository.findAll(PageRequest.of(start, pageSize, Sort.by("id").descending())).getContent();
		return documents;
	}

	public SendDocument getDocument(Long id) {
		return documentDaoRepository.findById(id).orElse(null);
	}

	public int updateStatuses(List<Long> idList, SendDocumentStatus status) {
		AtomicInteger count = new AtomicInteger(0);
		if (idList.size() > 0) {
			Iterable<SendDocument> documents = documentDaoRepository.findAllById(idList);
			documents.forEach(document -> {
				long start = System.currentTimeMillis();
				document.setDocumentStatus(status);
				documentDaoRepository.save(document);
				noticeInJournal(status, document, System.currentTimeMillis() - start);
				count.getAndIncrement();
			});
		}
		return count.get();
	}

	public int updateStatus(Long id, SendDocumentStatus status) {
		int count = 0;
		long start = System.currentTimeMillis();
		SendDocument document = documentDaoRepository.findById(id).get();
		if (document != null) {
			document.setDocumentStatus(status);
			documentDaoRepository.save(document);
			noticeInJournal(status, document, System.currentTimeMillis() - start);
			count++;
		}

		return count;
	}

	public SendDocument sendFile(Path path, boolean validate) throws Exception {
		long start = System.currentTimeMillis();
		DocumentInfo documentInfo;
    	File file = path.toFile();
		try (InputStream is = new FileInputStream(file) ) {
			documentInfo = documentParseService.parseDocumentInfo(is);
    	}

    	DocumentFormat documentFormat = documentFormatDetectService.defineDocumentFormat(documentInfo);
    	
    	if (documentFormat == DocumentFormat.UNSUPPORTED) {
    		throw new Exception("Document format is unsupported: "+documentInfo.getRoot()+", "+documentInfo.getProfile()+", "+documentInfo.getCustomizationID());
    	}
    	
    	if (validate) {
			List<RuleDocumentValidation> ruleByFormat = ruleService.getValidationRuleListByFormat(documentFormat);
			for (RuleDocumentValidation ruleDocumentValidation : ruleByFormat) {
				DocumentProcessStep step = documentValidationTransformationService.validateByRule(path, ruleDocumentValidation);
				if (!step.isSuccess()) {
					throw new Exception("Document is resolved as "+documentFormat+" but is not valid: "+step.getMessage());
				}
			}
    	}

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
    	sd.setDocumentType(documentFormat.getDocumentType());
    	sd.setReceiverIdRaw(documentInfo.getReceiver().encodeID());
    	sd.setSenderIdRaw(documentInfo.getSender().encodeID());
    	
    	documentDaoRepository.save(sd);
    	
		JournalSendDocument journalRecord = new JournalSendDocument();
		journalRecord.setSuccess(true);
		journalRecord.setDocument(sd);
		journalRecord.setType(SendDocumentProcessStepType.CREATE);
		journalRecord.setOrganisation(sd.getOrganisation());
		journalRecord.setMessage(MessageFormat.format("Uploaded manually with name ", path.getFileName()));
		journalRecord.setDurationMs(System.currentTimeMillis() - start);
		journalDocumentDaoRepository.save(journalRecord);
		
		try (InputStream is = new FileInputStream(file) ) {
			sendDocumentBytesStorageService.save(sd, SendDocumentBytesType.ORIGINAL, file.length(), is);
		}
		return sd;
    }

	private void noticeInJournal(SendDocumentStatus status, SendDocument document, long duration) {
		JournalSendDocument updateRecord = new JournalSendDocument();
		updateRecord.setDocument(document);
		updateRecord.setSuccess(true);
		updateRecord.setType(SendDocumentProcessStepType.MANUAL);
		updateRecord.setOrganisation(document.getOrganisation());
		updateRecord.setMessage(MessageFormat.format("Updated by user manually. Set status={0}.", status));
		updateRecord.setDurationMs(duration);
		journalDocumentDaoRepository.save(updateRecord);
	}

	public List<JournalSendDocument> getDocumentRecords(SendDocument document) {
		return journalDocumentDaoRepository.findByDocumentOrderByIdAsc(document);
	}

	@Override
	@Transactional(readOnly = true)
	public PageContainer<SendDocument> getAll(WebRequest webRequest) {
		return abstractGenerateDataService.generateDataPageContainer(SendDocument.class, webRequest, documentDaoRepository);
	}

	@Override
	@Transactional(readOnly = true)
	public SendDocument getOneById(long id) {
		return (SendDocument) abstractGenerateDataService.getOneById(id, SendDocument.class, documentDaoRepository);
	}

	public List<SendDocumentBytes> getDocumentBytes(SendDocument document) {
		return this.sendDocumentBytesStorageService.findAll(document);
	}
}
