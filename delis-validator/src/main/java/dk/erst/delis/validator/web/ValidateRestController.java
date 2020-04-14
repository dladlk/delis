package dk.erst.delis.validator.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.service.ValidateResult;
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

		DocumentProcessLog processLog = result.getProcessLog();
		List<DocumentProcessStep> stepList = processLog.getStepList();

		String status;
		int statusCode;
		String body;
		if (result.getProcessLog().isSuccess()) {
			statusCode = config.getResponseResponseValidCode();
			status = config.getResponseResponseValidStatus();

			StringBuilder sb = new StringBuilder();

			sb.append("Status: ");
			sb.append(status);

			sb.append(". Details: Validated as ");
			sb.append(result.getDocumentFormat());

			for (DocumentProcessStep step : stepList) {
				sb.append(";\n ");
				sb.append(step.getDescription());
				sb.append(" -  ");
				sb.append(step.isSuccess() ? "OK" : "ERROR");
			}
			body = sb.toString();
		} else {
			statusCode = config.getResponseResponseInvalidCode();

			DocumentProcessStep lastStep = stepList.get(stepList.size() - 1);
			boolean xsdValidation = false;
			if (lastStep.getStepType().isValidation() && lastStep.getStepType().isXsd()) {
				xsdValidation = true;
			}

			/*
			 * Unsupported format can mean also that XML is not valid - e.g. just TEXT is posted, so let's use it as a sign of non-xml data.
			 * 
			 * Of cause, it can also mean that we received unsupported format too...
			 */
			if (result.getDocumentFormat() == DocumentFormat.UNSUPPORTED) {
				status = config.getResponseResponseInvalidStatusXml();
			} else {
				status = xsdValidation ? config.getResponseResponseInvalidStatusXsd() : config.getResponseResponseInvalidStatusSchematron();
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Status: ");
			sb.append(status);

			sb.append(". Details: Validated as ");
			sb.append(result.getDocumentFormat());

			String lastStepDescription = lastStep.getDescription();
			if (lastStepDescription != null) {
				String annoyingPrefix = "Validate with ";
				if (lastStepDescription.startsWith(annoyingPrefix)) {
					lastStepDescription = lastStepDescription.substring(annoyingPrefix.length());
				}
			}
			sb.append("; Invalid by ");
			sb.append(lastStepDescription);
			if (lastStep.getMessage() != null) {
				sb.append(": ");
				sb.append(lastStep.getMessage());
			}
			List<ErrorRecord> errorRecords = lastStep.getErrorRecords();
			if (errorRecords != null && !errorRecords.isEmpty()) {
				for (ErrorRecord errorRecord : errorRecords) {
					if (!errorRecord.isWarning()) {
						sb.append("\n:\"");
						sb.append(errorRecord.getMessage());
						sb.append("\"\n");
						if (errorRecord.getDetailedLocation() != null) {
							sb.append(" at ");
							sb.append(errorRecord.getDetailedLocation());
						}
						break;
					}
				}
			}
			body = sb.toString();
		}

		BodyBuilder bodyBuilder = ResponseEntity.status(statusCode).header(config.getResponseStatusHeader(), status);
		return bodyBuilder.body(body);
	}
}
