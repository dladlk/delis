package dk.erst.delis.web.document;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;

@Service
public class DocumentService {
    private DocumentDaoRepository documentDaoRepository;
    private JournalDocumentDaoRepository journalDocumentDaoRepository;
    private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;
    private DocumentBytesStorageService documentBytesStorageService;

    @Autowired
    public DocumentService(DocumentDaoRepository documentDaoRepository,
                           JournalDocumentDaoRepository journalDocumentDaoRepository,
                           JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository,
                           DocumentBytesStorageService documentBytesStorageService
                           ) {
        this.documentDaoRepository = documentDaoRepository;
        this.journalDocumentDaoRepository = journalDocumentDaoRepository;
        this.journalDocumentErrorDaoRepository = journalDocumentErrorDaoRepository;
		this.documentBytesStorageService = documentBytesStorageService;
    }

    public List<Document> documentList (int start, int pageSize) {
        List<Document> documents = documentDaoRepository.findAll(PageRequest.of(start, pageSize, Sort.by("id").descending())).getContent();
        return documents;
    }

    public Document getDocument (Long id) {
        return documentDaoRepository.findById(id).orElse(null);
    }

    public int updateStatuses (List<Long> idList, DocumentStatus status) {
        AtomicInteger count = new AtomicInteger(0);
        if (idList.size() > 0) {
            Iterable<Document> documents = documentDaoRepository.findAllById(idList);
            documents.forEach(document -> {document.setDocumentStatus(status); noticeInJournal(status, document);count.getAndIncrement();});
            documentDaoRepository.saveAll(documents);
        }
        return count.get();
    }

    public int updateStatus (Long id, DocumentStatus status) {
        int count = 0;
        Document document = documentDaoRepository.findById(id).get();
        if (document != null) {
            document.setDocumentStatus(status);
            documentDaoRepository.save(document);
            noticeInJournal(status, document);
            count++;
        }

        return count;
    }

    private void noticeInJournal(DocumentStatus status, Document document) {
        JournalDocument updateRecord = new JournalDocument();
        updateRecord.setDocument(document);
        updateRecord.setSuccess(true);
        updateRecord.setType(DocumentProcessStepType.MANUAL);
        updateRecord.setOrganisation(document.getOrganisation());
        updateRecord.setMessage(MessageFormat.format("Updated by user manually. Set status={0}.", status));
        journalDocumentDaoRepository.save(updateRecord);
    }

    public List<JournalDocument> getDocumentRecords (Document document) {
        return journalDocumentDaoRepository.findByDocumentOrderByIdAsc(document);
    }

	public Map<Long, List<ErrorDictionary>> getErrorListByJournalDocumentIdMap(Document document) {
		List<JournalDocumentError> errorList = journalDocumentErrorDaoRepository.findAllByJournalDocumentDocumentOrderById(document);
		Map<Long, List<ErrorDictionary>> res = new HashMap<>();
		if (errorList != null) {
			for (JournalDocumentError journalDocumentError : errorList) {
				Long jdId = journalDocumentError.getJournalDocument().getId();
				ErrorDictionary error = journalDocumentError.getErrorDictionary();

				if (StringUtils.isNotEmpty(journalDocumentError.getDetailedLocation())) {
                    error.setLocation(journalDocumentError.getDetailedLocation());
                }

				List<ErrorDictionary> list = res.get(jdId);
				if (list == null) {
					list = new ArrayList<>();
					res.put(jdId, list);
				}
				list.add(error);
			}
		}
		return res;
	}
	
	public DocumentBytes findDocumentBytes(long documentId, long bytesId) {
		return this.documentBytesStorageService.find(documentId, bytesId);
	}

	public void getDocumentBytesContents(DocumentBytes documentBytes, ByteArrayOutputStream out) {
		this.documentBytesStorageService.load(documentBytes, out);
	}

}
