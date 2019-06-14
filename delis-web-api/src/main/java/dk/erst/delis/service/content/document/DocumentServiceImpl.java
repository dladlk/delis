package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.DocumentBytesRepository;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentRepository;
import dk.erst.delis.rest.data.request.document.UpdateDocumentStatusData;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Value("${delis.download.allow.all:#{false}}")
    private boolean downloadAllowAll;

    private final DocumentRepository documentRepository;
    private final DocumentBytesRepository documentBytesRepository;
    private final JournalDocumentRepository journalDocumentRepository;
    private final AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService;

    @Autowired
    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            DocumentBytesRepository documentBytesRepository,
            JournalDocumentRepository journalDocumentRepository,
            AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.documentBytesRepository = documentBytesRepository;
        this.journalDocumentRepository = journalDocumentRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<Document> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Document.class, webRequest, documentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, Document.class, documentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<DocumentBytes> findListDocumentBytesByDocumentId(Long documentId) {
        Document document = abstractGenerateDataService.getOneById(documentId, Document.class, documentRepository);
        long totalElements = documentBytesRepository.countByDocument(document);
        if (totalElements == 0) {
            return new ListContainer<>(Collections.emptyList());
        } else {
            return new ListContainer<>(documentBytesRepository.findByDocument(document));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void updateDocumentStatus(UpdateDocumentStatusData data) {
        List<Long> ids = data.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "you have not selected any document")));
        }
        if (data.getStatus() == null) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "you have not selected any document status")));
        }
        List<Document> documents = documentRepository.findByIdIn(ids);
        documents.forEach(document -> {
            document.setDocumentStatus(data.getStatus());
            noticeInJournal(data.getStatus(), document);
        });
        documentRepository.saveAll(documents);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public Path downloadFile(Long id, Long bytesId) {
        DocumentBytes documentBytes = documentBytesRepository.findByIdAndDocumentId(bytesId, id);
        if (documentBytes == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.NOT_FOUND.getReasonPhrase(), "Data not found")));
        }
        if (!isDownloadAllowed(documentBytes)) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("RECEIPT", HttpStatus.CONFLICT.getReasonPhrase(), "Only RECEIPT bytes are allowed for download, but " + documentBytes.getType() + " is requested")));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Path path = buildFile(documentBytes).toPath();
        try {
            Files.copy(path, outputStream);
            log.debug("Loaded data from " + path);
        } catch (IOException e) {
            log.error("Failed to read document bytes from " + path, e);
        }
        return path;
    }

    private void noticeInJournal(DocumentStatus status, Document document) {
        JournalDocument updateRecord = new JournalDocument();
        updateRecord.setDocument(document);
        updateRecord.setSuccess(true);
        updateRecord.setType(DocumentProcessStepType.MANUAL);
        updateRecord.setOrganisation(document.getOrganisation());
        updateRecord.setMessage(MessageFormat.format("Updated by user manually. Set status={0}.", status));
        journalDocumentRepository.save(updateRecord);
    }

    private boolean isDownloadAllowed(DocumentBytes b) {
        if (downloadAllowAll) {
            return true;
        }
        return b.getType() == DocumentBytesType.IN_AS4;
    }

    private File buildFile(DocumentBytes documentBytes) {
        Document document = documentBytes.getDocument();
        Organisation organisation = document.getOrganisation();
        String fileName = wrapZeros(documentBytes.getId(), 3) + "-" + documentBytes.getType()+".xml";
        Path rootFolder;
        if (organisation != null ) {
            rootFolder = Paths.get("/delis/document/LOADED", organisation.getCode());
        } else {
            rootFolder = Paths.get("/delis/document", "FAILED");
        }
        return Paths.get(rootFolder.toString(), wrapZeros(document.getId(), 5), fileName).toFile();
    }

    private String wrapZeros(Long id, int upToLength) {
        String s = String.valueOf(id);
        if (s.length() < upToLength) {
            s = "000000".substring(0, upToLength - s.length()) + s;
        }
        return s;
    }
}
