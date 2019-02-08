package dk.erst.delis.service.content.journal.document;

import java.util.List;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.service.content.AbstractService;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface JournalDocumentService extends AbstractService<JournalDocument> {

	List<JournalDocument> getByDocument(long documentId);
}
