package dk.erst.delis.web.organisation;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Organisation;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {

}
