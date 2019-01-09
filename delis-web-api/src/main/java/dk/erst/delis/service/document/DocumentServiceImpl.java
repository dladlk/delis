package dk.erst.delis.service.document;

import dk.erst.delis.data.*;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.document.DocumentData;
import dk.erst.delis.persistence.document.DocumentFilterModel;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.persistence.document.DocumentSpecification;
import dk.erst.delis.rest.data.response.PageContainer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;
import java.util.stream.Collectors;

import static dk.erst.delis.persistence.document.DocumentConstants.*;

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
            return getDefaultDocumentDataPageContainer(page, size, collectionSize);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<DocumentData> getAllAfterFilteringAndSorting(int page, int size, WebRequest webRequest) {

        long collectionSize = documentRepository.count();
        if (collectionSize == 0) {
            return new PageContainer<>();
        }

        List<String> filters = webRequest
                .getParameterMap()
                .keySet()
                .stream()
                .filter(key -> ObjectUtils.notEqual("0", webRequest.getParameter(key)))
                .filter(key -> ObjectUtils.notEqual("page", key) && ObjectUtils.notEqual("size", key))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(filters)) {

            DocumentFilterModel filterModel = new DocumentFilterModel();
            filterModel.setDocumentStatuses(Arrays.asList(DocumentStatus.values()));
            filterModel.setLastErrors(Arrays.asList(DocumentErrorCode.values()));
            filterModel.setDocumentFormats(Arrays.asList(DocumentFormat.values()));
            filterModel.setDocumentTypes(Arrays.asList(DocumentType.values()));
            filterModel.setStart(documentRepository.findMinCreateTime());
            filterModel.setEnd(documentRepository.findMaxCreateTime());

            for (String key : filters) {
                if (key.equals(ORGANIZATION_FIELD)) {
                    filterModel.setOrganisation(webRequest.getParameter(ORGANIZATION_FIELD));
                }
                if (key.equals(RECEIVER_IDENTIFIER_FIELD)) {
                    filterModel.setReceiver(webRequest.getParameter(RECEIVER_IDENTIFIER_FIELD));
                }
                if (key.equals(DOCUMENT_STATUS_FIELD)) {
                    filterModel.setDocumentStatuses(Collections.singletonList(DocumentStatus.valueOf(webRequest.getParameter(DOCUMENT_STATUS_FIELD))));
                }
                if (key.equals(LAST_ERROR_FIELD)) {
                    filterModel.setLastErrors(Collections.singletonList(DocumentErrorCode.valueOf(webRequest.getParameter(LAST_ERROR_FIELD))));
                }
                if (key.equals(DOCUMENT_TYPE_FIELD)) {
                    filterModel.setDocumentTypes(Collections.singletonList(DocumentType.valueOf(webRequest.getParameter(DOCUMENT_TYPE_FIELD))));
                }
                if (key.equals(INGOING_DOCUMENT_FORMAT_FIELD)) {
                    filterModel.setDocumentFormats(Collections.singletonList(DocumentFormat.valueOf(webRequest.getParameter(INGOING_DOCUMENT_FORMAT_FIELD))));
                }
                if (key.equals("start")) {
                    long startDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("start")));
                    filterModel.setStart(new Date(startDate));
                }
                if (key.equals("end")) {
                    long endDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("end")));
                    filterModel.setEnd(new Date(endDate));
                }
                if (key.equals(SENDER_NAME_FIELD)) {
                    filterModel.setSenderName(webRequest.getParameter(SENDER_NAME_FIELD));
                }
            }

            String sort = filters
                    .stream()
                    .filter(filter -> filter.startsWith("count"))
                    .findFirst().orElse(null);

            if (StringUtils.isNotBlank(sort)) {

                switch (sort) {
                    case COUNT_CLICK_ORGANIZATION_FIELD : {
                        int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_ORGANIZATION_FIELD)));
                        if (countClickOrganisation == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, ORGANIZATION_FIELD, filterModel);
                        } else if (countClickOrganisation == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, ORGANIZATION_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_RECEIVER_IDENTIFIER_FIELD : {
                        int countClickReceiver = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_RECEIVER_IDENTIFIER_FIELD)));
                        if (countClickReceiver == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, RECEIVER_IDENTIFIER_FIELD, filterModel);
                        } else if (countClickReceiver == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, RECEIVER_IDENTIFIER_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_DOCUMENT_STATUS_FIELD : {
                        int countClickStatus = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_DOCUMENT_STATUS_FIELD)));
                        if (countClickStatus == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, DOCUMENT_STATUS_FIELD, filterModel);
                        } else if (countClickStatus == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, DOCUMENT_STATUS_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_LAST_ERROR_FIELD : {
                        int countClickLastError = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_LAST_ERROR_FIELD)));
                        if (countClickLastError == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, LAST_ERROR_FIELD, filterModel);
                        } else if (countClickLastError == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, LAST_ERROR_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_DOCUMENT_TYPE_FIELD : {
                        int countClickIngoingFormat = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_DOCUMENT_TYPE_FIELD)));
                        if (countClickIngoingFormat == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, DOCUMENT_TYPE_FIELD, filterModel);
                        } else if (countClickIngoingFormat == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, DOCUMENT_TYPE_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_INGOING_DOCUMENT_FORMAT_FIELD : {
                        int countClickIngoingFormat = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_INGOING_DOCUMENT_FORMAT_FIELD)));
                        if (countClickIngoingFormat == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, INGOING_DOCUMENT_FORMAT_FIELD, filterModel);
                        } else if (countClickIngoingFormat == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, INGOING_DOCUMENT_FORMAT_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_CREATE_TIME_FIELD : {
                        int countClickReceived = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_CREATE_TIME_FIELD)));
                        if (countClickReceived == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, CREATE_TIME_FIELD, filterModel);
                        } else if (countClickReceived == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, CREATE_TIME_FIELD, filterModel);
                        }
                    } break;
                    case COUNT_CLICK_SENDER_NAME_FIELD : {
                        int countClickSenderName = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter(COUNT_CLICK_SENDER_NAME_FIELD)));
                        if (countClickSenderName == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, SENDER_NAME_FIELD, filterModel);
                        } else if (countClickSenderName == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, SENDER_NAME_FIELD, filterModel);
                        }
                    } break;
                }
            }

            return getDefaultDocumentDataPageContainerWithoutSorting(page, size, collectionSize, filterModel);
        }

        return getDefaultDocumentDataPageContainer(page, size, collectionSize);
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

    private PageContainer<DocumentData> getDefaultDocumentDataPageContainer(int page, int size, long collectionSize) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getDefaultDocumentDataPageContainerWithoutSorting(
            int page, int size, long collectionSize, DocumentFilterModel filterModel) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        DocumentSpecification.generateDocumentCriteriaPredicate(filterModel),
                        PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getDescendingDocumentDataPageContainer(
            int page, int size, long collectionSize, String param, DocumentFilterModel filterModel) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository
                        .findAll(
                                DocumentSpecification.generateDocumentCriteriaPredicate(filterModel),
                                PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getAscendingDocumentDataPageContainer(
            int page, int size, long collectionSize, String param, DocumentFilterModel filterModel) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository
                        .findAll(
                                DocumentSpecification.generateDocumentCriteriaPredicate(filterModel),
                                PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent()
        ));
    }

    private List<DocumentData> generateDocumentData(List<Document> documents) {
        return documents.stream().map(DocumentData::new).collect(Collectors.toList());
    }
}
