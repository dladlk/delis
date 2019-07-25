package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.service.content.AbstractService;

import org.springframework.web.context.request.WebRequest;

public interface JournalDocumentDelisWebApiService extends AbstractService<JournalDocument> {

	ListContainer<JournalDocument> getByDocument(WebRequest webRequest, long documentId);
	ListContainer<JournalDocumentError> getByJournalDocumentDocumentId(long documentId);
}
