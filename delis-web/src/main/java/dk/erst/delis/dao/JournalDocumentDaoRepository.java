package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JournalDocumentDaoRepository extends PagingAndSortingRepository<JournalDocument, Long> {

	List<JournalDocument> findByDocumentOrderByIdAsc(Document document);
}
