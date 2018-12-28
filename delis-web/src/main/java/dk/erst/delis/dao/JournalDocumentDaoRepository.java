package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Document;
import dk.erst.delis.data.JournalDocument;

public interface JournalDocumentDaoRepository extends PagingAndSortingRepository<JournalDocument, Long> {

	List<JournalDocument> findByDocumentOrderByIdAsc(Document document);
}
