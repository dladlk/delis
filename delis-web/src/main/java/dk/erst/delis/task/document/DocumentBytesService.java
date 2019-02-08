package dk.erst.delis.task.document;

import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentBytesService {
    private DocumentBytesDaoRepository documentBytesDaoRepository;

    @Autowired
    public DocumentBytesService(DocumentBytesDaoRepository documentBytesDaoRepository) {
        this.documentBytesDaoRepository = documentBytesDaoRepository;
    }

    public void saveDocumentBytes (DocumentBytes documentBytes) {
        documentBytesDaoRepository.save(documentBytes);
    }


    public DocumentBytes createLoaded (Document document, String location) {
        DocumentBytes documentBytes = getDocumentBytes(document, location, DocumentBytesType.IN, "Document loaded");
        return documentBytes;
    }

    public DocumentBytes createLoadedMetadata (Document document, String location) {
        DocumentBytesType type = DocumentBytesType.IN_AS4;
        String description = "Document matadata loaded";

        DocumentBytes documentBytes = getDocumentBytes(document, location, type, description);
        return documentBytes;
    }

    public DocumentBytes createLoadedSBD (Document document, String location) {
        DocumentBytes documentBytes = getDocumentBytes(document, location, DocumentBytesType.IN_SBD, "Document SBD loaded");
        return documentBytes;
    }

    private DocumentBytes getDocumentBytes(Document document, String location, DocumentBytesType type, String description) {
        DocumentBytes documentBytes = new DocumentBytes();
        documentBytes.setDocument(document);
        documentBytes.setType(type);
        documentBytes.setLocation(location);
        documentBytes.setDescription(description);
        return documentBytes;
    }


    public DocumentBytes createReadyDocumentBytes (Document document, String location) {
        DocumentBytes documentBytes = getDocumentBytes(document, location, DocumentBytesType.READY, "Document validated and ready for deliver");
        return documentBytes;
    }

    public DocumentBytes createDeliverDocumentBytes (Document document, String location) {
        DocumentBytes documentBytes = getDocumentBytes(document, location, DocumentBytesType.OUT, "Document exported to deliver location");
        return documentBytes;
    }

    public DocumentBytes findDocumentBytesValidated (Document document) {
        DocumentBytes documentBytes = documentBytesDaoRepository.findLastByDocumentAndType(document, DocumentBytesType.READY);
        return documentBytes;
    }

    public DocumentBytes findDocumentBytesLoaded (Document document) {
        DocumentBytes documentBytes = documentBytesDaoRepository.findLastByDocumentAndType(document, DocumentBytesType.IN);
        return documentBytes;
    }
}

