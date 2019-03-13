package dk.erst.delis.rest;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;

import io.swagger.annotations.Api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author funtusthan, created by 12.03.19
 */

@Api
@Slf4j
@RestController
@RequestMapping("/rest/document")
public class RestDocumentController {

    private final DocumentProcessService documentProcessService;
    private final DocumentLoadService documentLoadService;

    @Autowired
    public RestDocumentController(DocumentProcessService documentProcessService, DocumentLoadService documentLoadService) {
        this.documentProcessService = documentProcessService;
        this.documentLoadService = documentLoadService;
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam MultipartFile file, @RequestParam boolean validateImmediately) {
        File tempFile;
        try {
            tempFile = File.createTempFile("manual_upload_" + file.getName() + "_", ".xml");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                IOUtils.copy(file.getInputStream(), fos);
            }
        } catch (IOException e) {
            log.error("Failed to save uploaded file to temp for " + file.getName(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DocumentLoadResponse("Failed to save uploaded file to temp for " + file.getName()));
        }

        log.info("Created test file " + tempFile);
        Document document = documentLoadService.loadFile(tempFile.toPath());
        if (Objects.isNull(document)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DocumentLoadResponse("Cannot load file " + tempFile + " as document, see logs"));
        } else {
            if (document.getDocumentStatus().isLoadFailed()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DocumentLoadResponse("Uploaded file as a document with status " + document.getDocumentStatus()));
            } else {
                if (validateImmediately) {
                    StatData statData = new StatData();
                    documentProcessService.processDocument(statData, document);
                    return ResponseEntity.ok(new DocumentLoadResponse("Successfully uploaded file and validated: " + statData.toStatString()));
                } else {
                    return ResponseEntity.ok(new DocumentLoadResponse("Successfully uploaded file as a document with status " + document.getDocumentStatus()));
                }
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class DocumentLoadResponse {

        private String message;
    }
}
