package dk.erst.delis.dao;

import java.util.List;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JournalIdentifierDaoRepository extends PagingAndSortingRepository<JournalIdentifier, Long> {

	List<JournalIdentifier> findTop5ByIdentifierOrderByIdDesc(Identifier identifier);

}
