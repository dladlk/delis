package dk.erst.delis.rest.api.document;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.DocumentType;
import dk.erst.delis.rest.api.model.response.PageContainer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@Slf4j
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    @GetMapping
    public ResponseEntity getDocumentList(WebRequest webRequest) {
        return ResponseEntity.ok(getContainer(webRequest));
    }

    private PageContainer<TempDocument> getContainer(WebRequest webRequest) {

        List<TempDocument> documents = loadDocuments();
        if (documents != null) {
            PageContainer<TempDocument> container = new PageContainer<>();
            int page = 1;
            int size = 10;
            if (webRequest != null) {
                page = webRequest.getParameter("page") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("page"))) : 1;
                size = webRequest.getParameter("size") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("size"))) : 10;
                documents = sortingDocuments(documents, webRequest);
                documents = getDocumentsAfterFiltering(documents, webRequest);
            }
            container.setCollectionSize(documents.size());
            documents = sublistDocuments(documents, page, size);
            container.setCurrentPage(page);
            container.setItems(documents);
            container.setPageSize(size);
            return container;
        } else {
            return new PageContainer<>();
        }
    }

    private List<TempDocument> loadDocuments() {
        try {
            File file = ResourceUtils.getFile("classpath:static/json/documents.json");
            InputStream in = new FileInputStream(file);
            String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result, TempDocumentList.class).getDocs();
        } catch (IOException e) {
            return null;
        }
    }

    private List<TempDocument> getDocumentsAfterFiltering(List<TempDocument> documents, WebRequest webRequest) {

//        String received,
//        String issued,

        if (webRequest.getParameter("organisation") != null) {
            String organisation = webRequest.getParameter("organisation");
            documents = documents.stream().filter(document -> document.organisation.contains(Objects.requireNonNull(organisation))).collect(Collectors.toList());
        }

        if (webRequest.getParameter("receiver") != null) {
            String receiver = webRequest.getParameter("receiver");
            documents = documents.stream().filter(document -> document.receiver.contains(Objects.requireNonNull(receiver))).collect(Collectors.toList());
        }

        if (webRequest.getParameter("senderName") != null) {
            String senderName = webRequest.getParameter("senderName");
            documents = documents.stream().filter(document -> document.senderName.contains(Objects.requireNonNull(senderName))).collect(Collectors.toList());
        }

        if (webRequest.getParameter("receiverName") != null) {
            String receiverName = webRequest.getParameter("receiverName");
            documents = documents.stream().filter(document -> document.receiverName.contains(Objects.requireNonNull(receiverName))).collect(Collectors.toList());
        }

        if (webRequest.getParameter("status") != null) {
            try {
                DocumentStatus status = DocumentStatus.valueOf(webRequest.getParameter("status"));
                documents = documents.stream().filter(document -> document.status.equals(status)).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.error("parameter status is not valid: " + e.getMessage());
            }
        }

        if (webRequest.getParameter("lastError") != null) {
            try {
                DocumentErrorCode lastError = DocumentErrorCode.valueOf(webRequest.getParameter("lastError"));
                documents = documents.stream().filter(document -> document.lastError.equals(lastError)).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.error("parameter lastError is not valid: " + e.getMessage());
            }
        }

        if (webRequest.getParameter("documentType") != null) {
            try {
                DocumentType documentType = DocumentType.valueOf(webRequest.getParameter("documentType"));
                documents = documents.stream().filter(document -> document.documentType.equals(documentType)).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.error("parameter documentType is not valid: " + e.getMessage());
            }
        }

        if (webRequest.getParameter("ingoingFormat") != null) {
            try {
                DocumentFormat ingoingFormat = DocumentFormat.valueOf(webRequest.getParameter("ingoingFormat"));
                documents = documents.stream().filter(document -> document.ingoingFormat.equals(ingoingFormat)).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.error("parameter ingoingFormat is not valid: " + e.getMessage());
            }
        }

        return documents;
    }

    private List<TempDocument> sortingDocuments(List<TempDocument> documents, WebRequest webRequest) {

        if (webRequest.getParameter("countClickOrganisation") != null) {
            int countClickOrganisation = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickOrganisation")));
            if (countClickOrganisation == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.organisation)).collect(Collectors.toList());
            } else if (countClickOrganisation == 2) {
                return documents.stream().sorted((one, two) -> two.organisation.compareTo(one.organisation)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickReceiver") != null) {
            int countClickReceiver = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceiver")));
            if (countClickReceiver == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.receiver)).collect(Collectors.toList());
            } else if (countClickReceiver == 2) {
                return documents.stream().sorted((one, two) -> two.receiver.compareTo(one.receiver)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickStatus") != null) {
            int countClickStatus = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickStatus")));
            if (countClickStatus == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.status)).collect(Collectors.toList());
            } else if (countClickStatus == 2) {
                return documents.stream().sorted((one, two) -> two.status.compareTo(one.status)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickLastError") != null) {
            int countClickLastError = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickLastError")));
            if (countClickLastError == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.lastError)).collect(Collectors.toList());
            } else if (countClickLastError == 2) {
                return documents.stream().sorted((one, two) -> two.lastError.compareTo(one.lastError)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickDocumentType") != null) {
            int countClickDocumentType = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickDocumentType")));
            if (countClickDocumentType == 1) {
                documents = documents.stream().sorted(Comparator.comparing(one -> one.documentType)).collect(Collectors.toList());
            } else if (countClickDocumentType == 2) {
                return documents.stream().sorted((one, two) -> two.documentType.compareTo(one.documentType)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickIngoingFormat") != null) {
            int countClickIngoingFormat = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickIngoingFormat")));
            if (countClickIngoingFormat == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.ingoingFormat)).collect(Collectors.toList());
            } else if (countClickIngoingFormat == 2) {
                return documents.stream().sorted((one, two) -> two.ingoingFormat.compareTo(one.ingoingFormat)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickReceived") != null) {
            int countClickReceived = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceived")));
            if (countClickReceived == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.received)).collect(Collectors.toList());
            } else if (countClickReceived == 2) {
                return documents.stream().sorted((one, two) -> two.received.compareTo(one.received)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickIssued") != null) {
            int countClickIssued = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickIssued")));
            if (countClickIssued == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.issued)).collect(Collectors.toList());
            } else if (countClickIssued == 2) {
                return documents.stream().sorted((one, two) -> two.issued.compareTo(one.issued)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickSenderName") != null) {
            int countClickSenderName = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickSenderName")));
            if (countClickSenderName == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.senderName)).collect(Collectors.toList());
            } else if (countClickSenderName == 2) {
                return documents.stream().sorted((one, two) -> two.senderName.compareTo(one.senderName)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        if (webRequest.getParameter("countClickReceiverName") != null) {
            int countClickReceiverName = Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("countClickReceiverName")));
            if (countClickReceiverName == 1) {
                return documents.stream().sorted(Comparator.comparing(one -> one.receiverName)).collect(Collectors.toList());
            } else if (countClickReceiverName == 2) {
                return documents.stream().sorted((one, two) -> two.receiverName.compareTo(one.receiverName)).collect(Collectors.toList());
            } else {
                return loadDocuments();
            }
        }

        return documents;
    }

    private List<TempDocument> sublistDocuments(List<TempDocument> documents, int page, int size) {
        int currentPage = (--page) * size;
        size += currentPage;
        try {
            return documents.subList(currentPage, size);
        } catch (IndexOutOfBoundsException e) {
            return documents.subList(currentPage, documents.size());
        }
    }

    @Getter
    @Setter
    private static class TempDocumentList {

        private List<TempDocument> docs;
    }

    @Getter
    @Setter
    private static class TempDocument {

        private String organisation;
        private String receiver;
        private DocumentStatus status;
        private DocumentErrorCode lastError;
        private DocumentType documentType;
        private DocumentFormat ingoingFormat;
        private String received;
        private String issued;
        private String senderName;
        private String receiverName;
    }
}
