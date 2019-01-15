package dk.erst.delis.dao;

import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.organisation.Organisation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IdentifierGroupDaoRepository extends PagingAndSortingRepository<IdentifierGroup, Long> {

	IdentifierGroup findByOrganisationAndCode(Organisation organisation, String identifierGroupCode);

}
