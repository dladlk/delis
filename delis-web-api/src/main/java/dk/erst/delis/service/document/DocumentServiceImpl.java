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
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Objects;
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

    private PageContainer<DocumentData> getDefaultDocumentDataPageContainer(int page, int size, long collectionSize) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        PageRequest.of(page - 1, size, Sort.by("id").descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getDescendingDocumentDataPageContainer(int page, int size, long collectionSize, String param) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        PageRequest.of(page - 1, size, Sort.by(param).descending())).getContent()
        ));
    }

    private PageContainer<DocumentData> getAscendingDocumentDataPageContainer(int page, int size, long collectionSize, String param) {
        return new PageContainer<>(page, size, collectionSize, generateDocumentData(
                documentRepository.findAll(
                        PageRequest.of(page - 1, size, Sort.by(param).ascending())).getContent()
        ));
    }

    @Override
    public PageContainer<DocumentData> getAllAfterFiltering(int page, int size, WebRequest request) {
        return null;
    }

    @Override
    public PageContainer<DocumentData> getAllAfterSorting(int page, int size, WebRequest webRequest) {

        long collectionSize = documentRepository.count();
        if (collectionSize == 0) {
            return new PageContainer<>();
        }

        if (webRequest.getParameter("countClickOrganisation") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickOrganisation")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "organisation");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "organisation");
            }
        }

        if (webRequest.getParameter("countClickReceiver") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceiver")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "receiverIdentifier");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "receiverIdentifier");
            }
        }

        if (webRequest.getParameter("countClickStatus") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickStatus")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "documentStatus");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "documentStatus");
            }
        }

        if (webRequest.getParameter("countClickLastError") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickLastError")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "lastError");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "lastError");
            }
        }

        if (webRequest.getParameter("countClickDocumentType") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickDocumentType")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat");
            }
        }

        if (webRequest.getParameter("countClickIngoingFormat") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickIngoingFormat")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "ingoingDocumentFormat");
            }
        }

        if (webRequest.getParameter("countClickReceived") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceived")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "createTime");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "createTime");
            }
        }

        if (webRequest.getParameter("countClickSenderName") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickSenderName")));
            if (countClickOrganisation == 1) {
                return getDescendingDocumentDataPageContainer(page, size, collectionSize, "senderName");
            } else if (countClickOrganisation == 2) {
                return getAscendingDocumentDataPageContainer(page, size, collectionSize, "senderName");
            }
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

    private List<DocumentData> generateDocumentData(List<Document> documents) {
        return documents.stream().map(DocumentData::new).collect(Collectors.toList());
    }
}
