package dk.erst.delis.service.document;

import dk.erst.delis.data.*;
import dk.erst.delis.persistence.document.DocumentData;
import dk.erst.delis.persistence.document.DocumentRepository;
import dk.erst.delis.persistence.document.DocumentSpecification;
import dk.erst.delis.rest.data.response.PageContainer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.*;
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

            String organisation = null;
            String receiver = null;
            List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.values());
            List<DocumentErrorCode> lastErrors = Arrays.asList(DocumentErrorCode.values());
            String senderName = null;
            List<DocumentFormat> documentFormats = Arrays.asList(DocumentFormat.values());
            Date start = documentRepository.findMinCreateTime();
            Date end = documentRepository.findMaxCreateTime();

            for (String key : filters) {
                if (key.equals("organisation")) {
                    organisation = webRequest.getParameter("organisation");
                }
                if (key.equals("receiver")) {
                    receiver = webRequest.getParameter("receiver");
                }
                if (key.equals("status")) {
                    documentStatuses = Collections.singletonList(DocumentStatus.valueOf(webRequest.getParameter("status")));
                }
                if (key.equals("lastError")) {
                    lastErrors = Collections.singletonList(DocumentErrorCode.valueOf(webRequest.getParameter("lastError")));
                }
//                if (key.equals("documentType")) {
//                    ingoingDocumentFormats = Collections.singletonList(DocumentFormat.getDocumentFormatByDocumentType(webRequest.getParameter("documentType")));
//                }
                if (key.equals("ingoingFormat")) {
                    documentFormats = Collections.singletonList(DocumentFormat.valueOf(webRequest.getParameter("ingoingFormat")));
                }
                if (key.equals("start")) {
                    long startDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("start")));
                    start = new Date(startDate);
                }
                if (key.equals("end")) {
                    long endDate = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("end")));
                    end = new Date(endDate);
                }
                if (key.equals("senderName")) {
                    senderName = webRequest.getParameter("senderName");
                }
            }

            String sort = filters
                    .stream()
                    .filter(filter -> filter.startsWith("count"))
                    .findFirst().orElse(null);

            if (StringUtils.isNotBlank(sort)) {

                switch (sort) {
                    case "countClickOrganisation": {
                        int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickOrganisation")));
                        if (countClickOrganisation == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "organisation",
                                    organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickOrganisation == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "organisation", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                    case "countClickReceiver": {
                        int countClickReceiver = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceiver")));
                        if (countClickReceiver == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "receiverIdentifier", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickReceiver == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "receiverIdentifier", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                    case "countClickStatus": {
                        int countClickStatus = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickStatus")));
                        if (countClickStatus == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "documentStatus", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickStatus == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "documentStatus", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                    case "countClickLastError": {
                        int countClickLastError = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickLastError")));
                        if (countClickLastError == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "lastError", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickLastError == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "lastError", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
//                    case "countClickDocumentType" : {
//                        int countClickDocumentType = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickDocumentType")));
//                        if (countClickDocumentType == 1) {
//                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat", organisations, identifiers, documentStatuses, lastErrors, ingoingDocumentFormats,
//                                    start, end, senderNames);
//                        } else if (countClickDocumentType == 2) {
//                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat", organisations, identifiers, documentStatuses, lastErrors, ingoingDocumentFormats,
//                                    start, end, senderNames);
//                        }
//                    } break;
                    case "countClickIngoingFormat": {
                        int countClickIngoingFormat = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickIngoingFormat")));
                        if (countClickIngoingFormat == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickIngoingFormat == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                    case "countClickReceived": {
                        int countClickReceived = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceived")));
                        if (countClickReceived == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "createTime", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickReceived == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "createTime", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                    case "countClickSenderName": {
                        int countClickSenderName = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickSenderName")));
                        if (countClickSenderName == 1) {
                            return getDescendingDocumentDataPageContainer(page, size, collectionSize, "senderName", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        } else if (countClickSenderName == 2) {
                            return getAscendingDocumentDataPageContainer(page, size, collectionSize, "senderName", organisation,
                                    receiver,
                                    documentStatuses,
                                    lastErrors,
                                    senderName,
                                    documentFormats,
                                    start, end);
                        }
                    } break;
                }
            }

            return getDefaultDocumentDataPageContainerWithoutSorting(
                    page, size, collectionSize,
                    organisation,
                    receiver,
                    documentStatuses,
                    lastErrors,
                    senderName,
                    documentFormats,
                    start, end);
        }

        return getDefaultDocumentDataPageContainer(page, size, collectionSize);
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

    private PageContainer<DocumentData> getDefaultDocumentDataPageContainer(int page, int size, long collectionSize) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getDefaultDocumentDataPageContainerWithoutSorting(
            int page, int size, long collectionSize, String organisation,
            String receiver,
            List<DocumentStatus> documentStatuses,
            List<DocumentErrorCode> lastErrors,
            String senderName,
            List<DocumentFormat> documentFormats,
            Date start, Date end) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        DocumentSpecification.generateDocumentCriteriaPredicate(
                                organisation,
                                receiver,
                                documentStatuses,
                                lastErrors,
                                senderName,
                                documentFormats, start, end),
                        PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getDescendingDocumentDataPageContainer(
            int page, int size, long collectionSize, String param,
            String organisation,
            String receiver,
            List<DocumentStatus> documentStatuses,
            List<DocumentErrorCode> lastErrors,
            String senderName,
            List<DocumentFormat> documentFormats,
            Date start, Date end) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository
                        .findAll(
                                DocumentSpecification.generateDocumentCriteriaPredicate(
                                        organisation,
                                        receiver,
                                        documentStatuses,
                                        lastErrors,
                                        senderName,
                                        documentFormats, start, end),
                                PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getAscendingDocumentDataPageContainer(
            int page, int size, long collectionSize, String param,
            String organisation,
            String receiver,
            List<DocumentStatus> documentStatuses,
            List<DocumentErrorCode> lastErrors,
            String senderName,
            List<DocumentFormat> documentFormats,
            Date start, Date end) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository
                        .findAll(
                                DocumentSpecification.generateDocumentCriteriaPredicate(
                                        organisation,
                                        receiver,
                                        documentStatuses,
                                        lastErrors,
                                        senderName,
                                        documentFormats, start, end),
                                PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent()
        ));
    }

    private List<DocumentData> generateDocumentData(List<Document> documents) {
        return documents.stream().map(DocumentData::new).collect(Collectors.toList());
    }
}
