package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.JournalIdentifier;

public interface JournalIdentifierDaoRepository extends PagingAndSortingRepository<JournalIdentifier, Long> {

	List<JournalIdentifier> findTop5ByIdentifierOrderByIdDesc(Identifier identifier);

}
