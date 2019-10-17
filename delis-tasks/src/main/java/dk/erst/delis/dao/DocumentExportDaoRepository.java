package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.DocumentExport;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentExportStatus;

public interface DocumentExportDaoRepository extends PagingAndSortingRepository<DocumentExport, Long> {

	@Query("select s.organisation as organisation "
			+ "from DocumentExport s "
			+ "where s.status = ?1 "
			+ "group by s.organisation"
	)
	List<Organisation> loadDocumentExportStatusStat(DocumentExportStatus status);

	@Query("select s "
			+ "from DocumentExport s "
			+ "where s.status = ?1 "
			+ "and s.organisation = ?2 "
			+ "and s.id > ?3 "
			+ "order by s.id"
	)
	List<DocumentExport> findForExportCheck(DocumentExportStatus status, Organisation organisation, Long lastId, Pageable pageable);
}
