package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.JournalOrganisation;
import dk.erst.delis.data.Organisation;

public interface JournalOrganisationRepository extends PagingAndSortingRepository<JournalOrganisation, Long> {

	List<JournalOrganisation> findTop5ByOrganisationOrderByIdDesc(Organisation organisation);
}
