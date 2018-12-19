package dk.erst.delis.rest.api.document;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.DocumentType;
import dk.erst.delis.rest.api.model.response.PageContainer;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@Validated
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    @GetMapping
    public ResponseEntity getDocumentList(
            @RequestParam(name = "organisation", required = false) String organisation,
            @RequestParam(name = "receiver", required = false) String receiver,
            @RequestParam(name = "status", required = false) DocumentStatus status,
            @RequestParam(name = "lastError", required = false) DocumentErrorCode lastError,
            @RequestParam(name = "documentType", required = false) DocumentType documentType,
            @RequestParam(name = "ingoingFormat", required = false) DocumentFormat ingoingFormat,
            @RequestParam(name = "received", required = false) String received,
            @RequestParam(name = "issued", required = false) String issued,
            @RequestParam(name = "senderName", required = false) String senderName,
            @RequestParam(name = "receiverName", required = false) String receiverName,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "must be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "20")
            @Min(value = 1, message = "must be greater than or equal to 1")
            @Max(value = 100, message = "should not be greater than or equal to 100") int size) {

        File documents = null;
        try {
            documents = ResourceUtils.getFile("classpath:static/json/documents.json");
            InputStream in = new FileInputStream(documents);
            String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            ObjectMapper mapper = new ObjectMapper();
            TempDocumentList list = mapper.readValue(result, TempDocumentList.class);
            return ResponseEntity.ok(getContainer(list.getDocs(), page, size, organisation, receiver, status, lastError, documentType, ingoingFormat, received, issued, senderName, receiverName));
        } catch (FileNotFoundException e) {
            System.out.println("error = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("ok");
    }

    private PageContainer<TempDocument> getContainer(
            List<TempDocument> documents, int page, int size,
            String organisation,
            String receiver,
            DocumentStatus status,
            DocumentErrorCode lastError,
            DocumentType documentType,
            DocumentFormat ingoingFormat,
            String received,
            String issued,
            String senderName,
            String receiverName) {

        System.out.println("page = " + page);
        System.out.println("size = " + size);
        System.out.println("documents size = " + documents.size());

        PageContainer<TempDocument> container = new PageContainer<>();
        container.setCollectionSize(documents.size());

        documents = getDocumentsAfterFiltering(documents, page, size, organisation, receiver, status, lastError, documentType, ingoingFormat, received, issued, senderName, receiverName);

        container.setCurrentPage(page);
        container.setItems(documents);
        container.setPageSize(size);
        return container;
    }

    private List<TempDocument> getDocumentsAfterFiltering(
            List<TempDocument> documents, int page, int size,
            String organisation,
            String receiver,
            DocumentStatus status,
            DocumentErrorCode lastError,
            DocumentType documentType,
            DocumentFormat ingoingFormat,
            String received,
            String issued,
            String senderName,
            String receiverName) {

        page = page - 1;
        int currentPage = page * size;
        size += currentPage;

        System.out.println("currentPage = " + currentPage);
        System.out.println("size = " + size);

        return documents.subList(currentPage, size);
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
