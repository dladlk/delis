package dk.erst.delis.rest.api.document;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.DocumentType;
import dk.erst.delis.rest.api.model.response.PageContainer;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    @GetMapping
    public ResponseEntity getDocumentList(WebRequest webRequest) {
        return ResponseEntity.ok(getContainer(webRequest));
    }

    private PageContainer<TempDocument> getContainer(WebRequest webRequest) {

        List<TempDocument> documents;
        try {
            File file = ResourceUtils.getFile("classpath:static/json/documents.json");
            InputStream in = new FileInputStream(file);
            String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            ObjectMapper mapper = new ObjectMapper();
            documents = mapper.readValue(result, TempDocumentList.class).getDocs();
        } catch (IOException e) {
            return new PageContainer<>();
        }

        PageContainer<TempDocument> container = new PageContainer<>();
        container.setCollectionSize(documents.size());

        int page = 1;
        int size = 10;

        if (webRequest != null) {
            page = webRequest.getParameter("page") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("page"))) : 1;
            size = webRequest.getParameter("size") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("size"))) : 10;
        }

        documents = getDocumentsAfterFiltering(documents, webRequest, page, size);

        container.setCurrentPage(page);
        container.setItems(documents);
        container.setPageSize(size);
        return container;
    }

    private List<TempDocument> getDocumentsAfterFiltering(
            List<TempDocument> documents, WebRequest webRequest, int page, int size) {

//        String organisation,
//        String receiver,
//        DocumentStatus status,
//        DocumentErrorCode lastError,
//        DocumentType documentType,
//        DocumentFormat ingoingFormat,
//        String received,
//        String issued,
//        String senderName,
//        String receiverName

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
