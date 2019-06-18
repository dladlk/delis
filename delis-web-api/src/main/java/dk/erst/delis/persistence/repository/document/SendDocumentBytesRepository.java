package dk.erst.delis.persistence.repository.document;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.persistence.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SendDocumentBytesRepository extends AbstractRepository<SendDocumentBytes> {

    List<SendDocumentBytes> findByDocument(SendDocument document);
    SendDocumentBytes findByIdAndDocumentId(Long id, Long sendDocumentId);
}
