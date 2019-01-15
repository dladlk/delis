package dk.erst.delis.service.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.document.DocumentFilterModel;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.persistence.document.DocumentSpecification;
import dk.erst.delis.rest.data.request.param.DateRequestModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

import static dk.erst.delis.persistence.document.DocumentConstants.*;

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
            return new PageContainer<>();
        }

        List<String> filters = WebRequestUtil.existParameters(webRequest);

        if (CollectionUtils.isNotEmpty(filters)) {

            DocumentFilterModel filterModel = new DocumentFilterModel();

            DateRequestModel dateRequestModel = WebRequestUtil.generateDateRequestModel(webRequest);
            if (Objects.nonNull(dateRequestModel)) {
                filterModel.setStart(dateRequestModel.getStart());
                filterModel.setEnd(dateRequestModel.getEnd());
            } else {
                filterModel.setStart(documentRepository.findMinCreateTime());
                filterModel.setEnd(documentRepository.findMaxCreateTime());
            }

            for (String key : filters) {
                if (key.equals(ORGANIZATION_FIELD)) {
                    filterModel.setOrganisation(webRequest.getParameter(ORGANIZATION_FIELD));
                }
                if (key.equals(RECEIVER_IDENTIFIER_FIELD)) {
                    filterModel.setReceiver(webRequest.getParameter(RECEIVER_IDENTIFIER_FIELD));
                }
                if (key.equals(DOCUMENT_STATUS_FIELD)) {
                    filterModel.setDocumentStatus(DocumentStatus.valueOf(webRequest.getParameter(DOCUMENT_STATUS_FIELD)));
                }
                if (key.equals(LAST_ERROR_FIELD)) {
                    filterModel.setLastError(DocumentErrorCode.valueOf(webRequest.getParameter(LAST_ERROR_FIELD)));
                }
                if (key.equals(DOCUMENT_TYPE_FIELD)) {
                    filterModel.setDocumentType(DocumentType.valueOf(webRequest.getParameter(DOCUMENT_TYPE_FIELD)));
                }
                if (key.equals(INGOING_DOCUMENT_FORMAT_FIELD)) {
                    filterModel.setIngoingDocumentFormat(DocumentFormat.valueOf(webRequest.getParameter(INGOING_DOCUMENT_FORMAT_FIELD)));
                }
                if (key.equals(SENDER_NAME_FIELD)) {
                    filterModel.setSenderName(webRequest.getParameter(SENDER_NAME_FIELD));
                }
            }

            String sort = WebRequestUtil.existSortParameter(filters);

            if (StringUtils.isNotBlank(sort)) {
                return abstractGenerateDataService.sortProcess(Document.class, sort, webRequest, page, size, collectionSize, documentRepository, filterModel, new DocumentSpecification());
            } else {
                return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, documentRepository, filterModel, new DocumentSpecification());
            }
        }

        return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, documentRepository);
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
