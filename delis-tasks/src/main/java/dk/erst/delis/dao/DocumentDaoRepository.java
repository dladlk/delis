package dk.erst.delis.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentStatus;

public interface DocumentDaoRepository extends PagingAndSortingRepository<Document, Long>, DocumentDao {

	@Query("select s.documentStatus as documentStatus, count(*) as documentCount "
			+ "from Document s "
			+ "group by s.documentStatus "
			) 
	List<Map<String, Object>> loadDocumentStatusStat();
	
	List<Document> findTop5ByDocumentStatusOrderByIdAsc(DocumentStatus documentStatus);

	@Query("select s.organisation as organisation "
			+ "from Document s "
			+ "where s.documentStatus = ?1 "
			+ "group by s.organisation"
	)
	List<Organisation> loadDocumentStatusStat(DocumentStatus documentStatus);

	@Query("select s "
			+ "from Document s "
			+ "where s.documentStatus = ?1 "
			+ "and s.organisation = ?2 "
			+ "and s.id > ?3 "
			+ "order by s.id"
	)
	List<Document> findForExport(DocumentStatus documentStatus, Organisation organisation, Long lastFailedInCurrentProcessing);

	Document findTop1ByOrganisation(Organisation dbOrganisation);

}
