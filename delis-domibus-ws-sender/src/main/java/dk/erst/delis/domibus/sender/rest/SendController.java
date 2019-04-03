package dk.erst.delis.domibus.sender.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dk.erst.delis.domibus.sender.service.SendService;
import dk.erst.delis.domibus.sender.service.SendWSResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Api
@Slf4j
@RestController
@RequestMapping("/rest")
public class SendController {

	private SendService sendService;

	@Autowired
	public SendController(SendService sendService) {
		this.sendService = sendService;
	}

	@PostMapping("/send")
	public ResponseEntity<SendWSResponse> send(@RequestParam MultipartFile file) {
		long start = System.currentTimeMillis();
		ResponseEntity<SendWSResponse> r = null;
		try {
			r = sendFile(file);
		} finally {
			log.info("Processed request in " + (System.currentTimeMillis() - start) + " ms with result: status=" + r.getStatusCodeValue() + ", body=" + r.getBody());
		}
		return r;
	}

	private ResponseEntity<SendWSResponse> sendFile(@RequestParam MultipartFile file) {
		SendWSResponse r = new SendWSResponse();

		File tempFile;
		try {
			tempFile = save(file);
		} catch (IOException e) {
			log.error("Failed to save file for " + file.getName(), e);

			r.addError("Failed to read data: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(r);
		}
		log.info("Created temporary file " + tempFile);

		try {
			r = this.sendService.send(tempFile);
		} catch (Exception e) {
			log.error("Failed to send file " + tempFile, e);
			r.addError("Failed to send: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(r);
		}
		return ResponseEntity.ok(r);
	}

	private File save(MultipartFile file) throws IOException {
		File tempFile = File.createTempFile("send_" + file.getName() + "_", ".xml");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			IOUtils.copy(file.getInputStream(), fos);
		}
		return tempFile;
	}
}
