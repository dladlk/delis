package dk.erst.delis.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.SyncOrganisationFact;

public interface SyncOrganisationFactRepository extends PagingAndSortingRepository<SyncOrganisationFact, Long> {

}
