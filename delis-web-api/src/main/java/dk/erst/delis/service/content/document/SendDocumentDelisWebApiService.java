package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.service.content.AbstractService;

public interface SendDocumentDelisWebApiService extends AbstractService<SendDocument> {

    ListContainer<SendDocumentBytes> findListSendDocumentBytesBySendDocumentId(long id);
    ListContainer<JournalSendDocument> findListJournalSendDocumentBySendDocumentId(long id);
    byte[] downloadFile(Long id, Long bytesId);
}
