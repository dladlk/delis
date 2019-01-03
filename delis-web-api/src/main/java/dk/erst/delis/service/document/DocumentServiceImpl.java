package dk.erst.delis.service.document;

import dk.erst.delis.data.Document;
import dk.erst.delis.persistence.document.DocumentData;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<DocumentData> getAll(int page, int size) {
        long collectionSize = documentRepository.count();
        if (collectionSize == 0) {
            return new PageContainer<>();
        } else {
            return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                    documentRepository.findAll(
                            PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null) {
            throw new RuntimeException();
        }
        return document;
    }

    private List<DocumentData> generateDocumentData(List<Document> documents) {
        return documents.stream().map(DocumentData::new).collect(Collectors.toList());
    }
}
