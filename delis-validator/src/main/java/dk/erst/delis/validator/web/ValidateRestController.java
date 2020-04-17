package dk.erst.delis.validator.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.service.ValidateRestResult;
import dk.erst.delis.validator.service.ValidateResult;
import dk.erst.delis.validator.service.ValidateResultStatus;
import dk.erst.delis.validator.service.ValidateService;
import dk.erst.delis.validator.service.input.MultipartFileInput;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ValidateRestController {

	@Autowired
	private DelisValidatorConfig config;

	@Autowired
	private ValidateService validateService;

	@PostMapping("rest/validate")
	public ResponseEntity<String> validate(

			@RequestParam("file") MultipartFile file,

			@RequestParam(name = "skipPEPPOL", required = false) boolean skipPEPPOL

	) {

		log.info("Requested to validate file " + file.getOriginalFilename());

		MultipartFileInput input = new MultipartFileInput(file);
		ValidateResult result = validateService.validateFile(input, skipPEPPOL);

		log.info("Validation result: " + result);

		ValidateRestResult restResult = result.getRestResult();
		BodyBuilder bodyBuilder = ResponseEntity.status(restResult.getHttpStatusCode()).header(config.getResponseStatusHeader(), convertStatusToText(restResult.getStatus()));
		return bodyBuilder.body(restResult.getBody());
	}

	private String convertStatusToText(ValidateResultStatus status) {
		switch (status) {
		case OK:
			return this.config.getResponseResponseValidStatus();
		case INVALID_XML:
			return this.config.getResponseResponseInvalidStatusXml();
		case INVALID_XSD:
			return this.config.getResponseResponseInvalidStatusXsd();
		case INVALID_SCH:
			return this.config.getResponseResponseInvalidStatusSchematron();
		}
		return null;
	}

}
