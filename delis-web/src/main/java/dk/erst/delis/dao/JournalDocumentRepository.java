package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.JournalDocument;

public interface JournalDocumentRepository extends PagingAndSortingRepository<JournalDocument, Long> {

}
