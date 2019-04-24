package dk.erst.delis.sender.service.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.journal.JournalSendDocument;

public interface JournalSendDocumentDaoRepository extends PagingAndSortingRepository<JournalSendDocument, Long> {

	List<JournalSendDocument> findByDocumentOrderByIdAsc(SendDocument sendDocument);
}
