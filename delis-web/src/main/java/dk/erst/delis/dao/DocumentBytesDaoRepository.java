package dk.erst.delis.dao;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DocumentBytesDaoRepository extends PagingAndSortingRepository<DocumentBytes, Long>, DocumentDao {

	DocumentBytes findLastByDocumentAndType(Document document, DocumentBytesType type);

	List<DocumentBytes> findByDocument(Document document);
}
