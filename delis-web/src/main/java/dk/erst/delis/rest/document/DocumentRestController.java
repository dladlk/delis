package dk.erst.delis.rest.document;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.erst.delis.data.DocumentErrorCode;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.data.DocumentType;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iehor Funtusov, created by 18.12.18
 */

@RestController
@RequestMapping("/rest/document")
public class DocumentRestController {

    @GetMapping
    public ResponseEntity getDocumentList() {

        File documents = null;
        try {
            documents = ResourceUtils.getFile("classpath:./static/json/documents.json");
            InputStream in = new FileInputStream(documents);
            String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            ObjectMapper mapper = new ObjectMapper();
            TempDocumentList list = mapper.readValue(result, TempDocumentList.class);
            return ResponseEntity.ok(list);
        } catch (FileNotFoundException e) {
            System.out.println("error = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("ok");
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
