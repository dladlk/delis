package dk.erst.delis.service.swagger;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author funtusthan, created by 12.03.19
 */

@Service
public class DocumentUploadService {

    public DocumentLoadResponse upload(MultipartFile file, boolean validateImmediately) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("Content-disposition", "form-data; name=file; filename=" + file.getOriginalFilename());
        HttpEntity<byte[]> doc;
        try {
            doc = new HttpEntity<>(file.getBytes(), headerMap);
        } catch (IOException e) {
            e.printStackTrace();
            return new DocumentLoadResponse(400, "failed");
        }

        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
        multipartReqMap.add("file", doc);

        HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(multipartReqMap, headers);
        ResponseEntity<String> resE = new RestTemplate()
                .exchange("http://localhost:8080/delis/rest/document/upload?validateImmediately=" + validateImmediately,
                        HttpMethod.POST, reqEntity, String.class); // todo how to get base url of delis-web

        return new DocumentLoadResponse(200, resE.getBody());
    }
}
