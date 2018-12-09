package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.Organisation;

public interface IdentifierRepository extends PagingAndSortingRepository<Identifier, Long> {

	Identifier findByOrganisationAndValueAndType(Organisation organisation, String value, String type);

}
