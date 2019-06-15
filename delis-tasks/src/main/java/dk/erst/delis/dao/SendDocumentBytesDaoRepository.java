package dk.erst.delis.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;

public interface SendDocumentBytesDaoRepository extends PagingAndSortingRepository<SendDocumentBytes, Long> {

	SendDocumentBytes findTop1ByDocumentAndTypeOrderByIdDesc(SendDocument document, SendDocumentBytesType type);

	List<SendDocumentBytes> findByDocument(SendDocument document);
	
	SendDocumentBytes findByDocumentIdAndId(long id, long documentId);
}
