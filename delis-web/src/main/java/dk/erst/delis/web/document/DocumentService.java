package dk.erst.delis.web.document;

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DocumentService {
    private DocumentDaoRepository documentDaoRepository;
    private JournalDocumentDaoRepository journalDocumentDaoRepository;

    @Autowired
    public DocumentService(DocumentDaoRepository documentDaoRepository, JournalDocumentDaoRepository journalDocumentDaoRepository) {
        this.documentDaoRepository = documentDaoRepository;
        this.journalDocumentDaoRepository = journalDocumentDaoRepository;
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
        updateRecord.setType(DocumentProcessStepType.RESOLVE_TYPE);
        updateRecord.setOrganisation(document.getOrganisation());
        updateRecord.setMessage(MessageFormat.format("Updated by user manually. Set status={0}.", status));
        journalDocumentDaoRepository.save(updateRecord);
    }

    public List<JournalDocument> getDocumentRecords (Document document) {
        return journalDocumentDaoRepository.findByDocumentOrderByIdAsc(document);
    }
}
