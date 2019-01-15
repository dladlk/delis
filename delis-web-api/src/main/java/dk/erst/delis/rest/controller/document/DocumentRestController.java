package dk.erst.delis.rest.controller.document;

import dk.erst.delis.rest.data.request.param.PageAndSizeModel;
import dk.erst.delis.service.document.DocumentService;
import dk.erst.delis.util.WebRequestUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.Min;

/**
 * @author Iehor Funtusov, created by 21.12.18
 */

@Slf4j
@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    private final DocumentService documentService;

    @Autowired
    public DocumentRestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity getDocumentList(WebRequest webRequest) {
        PageAndSizeModel pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(webRequest);
        return ResponseEntity.ok(documentService.getAllAfterFilteringAndSorting(pageAndSizeModel.getPage(), pageAndSizeModel.getSize(), webRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@PathVariable @Min(1) long id) {
        return ResponseEntity.ok(documentService.getOneById(id));
    }
}
