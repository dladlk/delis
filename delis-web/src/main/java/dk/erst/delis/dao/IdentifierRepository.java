package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Identifier;

public interface IdentifierRepository extends PagingAndSortingRepository<Identifier, Long> {

}
