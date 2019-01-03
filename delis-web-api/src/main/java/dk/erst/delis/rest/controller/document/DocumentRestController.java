package dk.erst.delis.rest.controller.document;

import dk.erst.delis.data.*;
import dk.erst.delis.persistence.document.DocumentData;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.document.DocumentService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

import java.util.Objects;

/**
 * @author Iehor Funtusov, created by 21.12.18
 */

@Slf4j
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public ResponseEntity getDocumentList(WebRequest webRequest) {
        return ResponseEntity.ok(getContainer(webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(getOneDocumentById(id));
    }

    private Document getOneDocumentById(long id) {
        return documentService.getOneById(id);
    }

    private PageContainer<DocumentData> getContainer(WebRequest webRequest) {

        int page = 1;
        int size = 10;
        if (webRequest != null) {
            page = webRequest.getParameter("page") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("page"))) : 1;
            size = webRequest.getParameter("size") != null ? Integer.valueOf(Objects.requireNonNull(webRequest.getParameter("size"))) : 10;
        }

        return documentService.getAll(page, size);
    }
}

