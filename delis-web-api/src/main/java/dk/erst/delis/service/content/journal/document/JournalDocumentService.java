package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractService;

import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface JournalDocumentService extends AbstractService<JournalDocument> {

	PageContainer<JournalDocument> getByDocument(WebRequest webRequest, long documentId);
}
