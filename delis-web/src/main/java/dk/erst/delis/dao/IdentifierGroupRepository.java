package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.IdentifierGroup;
import dk.erst.delis.data.Organisation;

public interface IdentifierGroupRepository extends PagingAndSortingRepository<IdentifierGroup, Long> {

	IdentifierGroup findByOrganisationAndCode(Organisation organisation, String identifierGroupCode);

}
