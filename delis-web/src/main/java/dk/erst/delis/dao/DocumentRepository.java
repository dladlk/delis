package dk.erst.delis.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.Document;

public interface DocumentRepository extends PagingAndSortingRepository<Document, Long> {

	@Query("select s.documentStatus as documentStatus, count(*) as documentCount "
			+ "from Document s "
			+ "group by s.documentStatus "
			) 
	List<Map<String, Object>> loadDocumentStatusStat();
	
}
