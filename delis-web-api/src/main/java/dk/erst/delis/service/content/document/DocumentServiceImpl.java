package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, AbstractGenerateDataService<DocumentRepository,Document> abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<Document> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Document.class, webRequest, documentRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, Document.class, documentRepository);
    }
}
