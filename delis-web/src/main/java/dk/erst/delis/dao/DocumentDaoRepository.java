package dk.erst.delis.dao;

import java.util.List;
import java.util.Map;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DocumentDaoRepository extends PagingAndSortingRepository<Document, Long>, DocumentDao {

	@Query("select s.documentStatus as documentStatus, count(*) as documentCount "
			+ "from Document s "
			+ "group by s.documentStatus "
			) 
	List<Map<String, Object>> loadDocumentStatusStat();
	
	List<Document> findTop5ByDocumentStatusOrderByIdAsc(DocumentStatus documentStatus);
}
