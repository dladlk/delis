package dk.erst.delis.rest.controller.swagger;

import dk.erst.delis.rest.data.response.DataContainer;
import dk.erst.delis.service.swagger.DocumentLoadResponse;
import dk.erst.delis.service.swagger.DocumentUploadService;

import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author funtusthan, created by 12.03.19
 */

@Api(value = "DocumentUploadController")
@RestController
@RequestMapping("/swagger/document")
public class DocumentUploadController {

    private final DocumentUploadService documentUploadService;

    @Autowired
    public DocumentUploadController(DocumentUploadService documentUploadService) {
        this.documentUploadService = documentUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam MultipartFile file, @RequestParam boolean validateImmediately) {
        DocumentLoadResponse loadResponse = documentUploadService.upload(file, validateImmediately);
        switch (loadResponse.getStatus()) {
            case 400 : return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loadResponse);
            default : return ResponseEntity.ok(new DataContainer<>(loadResponse));
        }
    }
}
