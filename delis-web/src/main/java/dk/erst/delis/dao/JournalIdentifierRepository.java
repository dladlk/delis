package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.JournalIdentifier;

public interface JournalIdentifierRepository extends PagingAndSortingRepository<JournalIdentifier, Long> {

}
