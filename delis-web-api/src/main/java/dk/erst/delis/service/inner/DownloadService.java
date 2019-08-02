package dk.erst.delis.service.inner;

import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;

@Slf4j
@Service
public class DownloadService {

    public ResponseEntity<Object> downloadFile(byte[] data, String fileName) {
        ResponseEntity.BodyBuilder resp = generateBodyBuilder(fileName);
        return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
    }

    public ResponseEntity<Object> downloadFile(File file, String fileName) {
        ResponseEntity.BodyBuilder resp = generateBodyBuilder(fileName);
        try {
            return resp.body(new InputStreamResource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            log.error("Failed to generate without sending", e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to generate without sending: " + e.getMessage())));
        }
    }

    private ResponseEntity.BodyBuilder generateBodyBuilder(String fileName) {
        ResponseEntity.BodyBuilder resp = ResponseEntity.ok();
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        resp.headers(headers);
        resp.contentType(MediaType.parseMediaType("application/octet-stream"));
        return resp;
    }
}
