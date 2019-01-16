package dk.erst.delis.service.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.documentRepository = documentRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<Document> getAllAfterFilteringAndSorting(int page, int size, WebRequest webRequest) {

        long collectionSize = documentRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, documentRepository);
        }

        String sort = WebRequestUtil.existSortParameter(webRequest);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(Document.class, sort, webRequest, page, size, collectionSize, documentRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(Document.class, webRequest, page, size, collectionSize, documentRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "document not found by id")));
        }
        return document;
    }
}
