package dk.erst.delis.task.document.response;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.xml.builder.ApplicationResponseBuilder;
import dk.erst.delis.xml.builder.data.ApplicationResponseData;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.LineResponse;
import dk.erst.delis.xml.builder.data.Response;
import dk.erst.delis.xml.builder.data.ResponseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplicationResponseService {

	private DocumentBytesStorageService documentBytesStorageService;
	private DocumentValidationTransformationService validationTransformationService;
	private ApplicationResponseBuilder builder = new ApplicationResponseBuilder();

	@Autowired
	public ApplicationResponseService(DocumentBytesStorageService documentBytesStorageService, DocumentValidationTransformationService validationTransformationService) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.validationTransformationService = validationTransformationService;
	}

	@Data
	public abstract static class ApplicationResponseGenerationData {

	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class InvoiceResponseGenerationData extends ApplicationResponseGenerationData {
		private String status;
		private String action;
		private boolean actionEnabled;
		private String action2;
		private boolean action2Enabled;
		private String reason;
		private boolean reasonEnabled;
		private String detailType;
		private String detailValue;
		private String statusReasonText;
		private String effectiveDate;
		private boolean effectiveDateEnabled;
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class MessageLevelResponseGenerationData extends ApplicationResponseGenerationData {
		private String type;
		private String description;
		private List<MessageLevelLineResponse> lineResponseList;
	}

	@Data
	public static class MessageLevelLineResponse {
		private String lineId;
		private String lineCode;
		private String description;
		private String reasonCode;
	}

	public boolean generateApplicationResponse(Document document, ApplicationResponseGenerationData data, OutputStream out) throws ApplicationResponseGenerationException {
		long start = System.currentTimeMillis();
		try {
			log.info("Started ApplicatResponse generation for document " + document);

			DocumentFormatFamily ingoingFamily = document.getIngoingDocumentFormat().getDocumentFormatFamily();

			if (ingoingFamily != DocumentFormatFamily.BIS3 || ingoingFamily != DocumentFormatFamily.CII) {
				throw new ApplicationResponseGenerationException(document.getId(), "ApplicationResponse can be generated only for ingoing formats CII or BIS3, but current document ingoing format was " + ingoingFamily.getCode());
			}

			log.info("Search for document bytes with format BIS3 ");
			DocumentBytes documentBytes = documentBytesStorageService.find(document, DocumentFormat.BIS3_INVOICE);
			if (documentBytes == null) {
				documentBytes = documentBytesStorageService.find(document, DocumentFormat.BIS3_CREDITNOTE);
			}
			log.info("Found document bytes: " + documentBytes);
			if (documentBytes == null) {
				throw new ApplicationResponseGenerationException(document.getId(), "Cannot find document data in format family BIS3");
			}

			ByteArrayOutputStream bisOutput = new ByteArrayOutputStream();
			documentBytesStorageService.load(documentBytes, bisOutput);

			byte[] xmlBytes = bisOutput.toByteArray();
			log.info("Loaded " + xmlBytes.length + " bytes");

			ByteArrayOutputStream irOutput = new ByteArrayOutputStream();
			try {
				builder.extractBasicData(new ByteArrayInputStream(xmlBytes), irOutput);
			} catch (Exception e) {
				log.error("Failed to extract basic data from BIS3 format", e);
				throw new ApplicationResponseGenerationException(document.getId(), "Failed to extract information about sender/receiver from BIS3 format: " + e.getMessage());
			}

			byte[] irBytes = irOutput.toByteArray();

			Date currentTime = Calendar.getInstance().getTime();
			SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

			ApplicationResponseData irData = new ApplicationResponseData();
			irData.setIssueDate(dateFormat.format(currentTime));
			irData.setIssueTime(timeFormat.format(currentTime));
			irData.setId(idFormat.format(currentTime) + "-" + document.getId());

			Response response = Response.builder().build();

			List<LineResponse> lineResponseList = null;
			if (data instanceof InvoiceResponseGenerationData) {
				InvoiceResponseGenerationData invoiceResponseData = (InvoiceResponseGenerationData) data;
				/*
				 * Important to generate OPStatusReason before OPStatusAction, so details are placed into it
				 */
				if (invoiceResponseData.isReasonEnabled()) {
					ResponseStatus responseStatus = ResponseStatus.builder().build();
					responseStatus.setStatusReasonCode(invoiceResponseData.getReason());
					responseStatus.setStatusReasonCodeListId("OPStatusReason");
					response.addStatus(responseStatus);
				}

				if (invoiceResponseData.isActionEnabled()) {
					ResponseStatus responseStatus = ResponseStatus.builder().build();
					responseStatus.setStatusReasonCode(invoiceResponseData.getAction());
					responseStatus.setStatusReasonCodeListId("OPStatusAction");
					response.addStatus(responseStatus);
				}
				if (invoiceResponseData.isAction2Enabled()) {
					ResponseStatus responseStatus = ResponseStatus.builder().build();
					responseStatus.setStatusReasonCode(invoiceResponseData.getAction2());
					responseStatus.setStatusReasonCodeListId("OPStatusAction");
					response.addStatus(responseStatus);
				}

				/*
				 * DetailType/DetailValue/StatusReason are always placed to first ResponseStatus - which is OPStatusReason if defined.
				 */
				if (isNotBlank(invoiceResponseData.getDetailType())) {
					response.getStatusOrCreate(0).setConditionAttributeID(invoiceResponseData.getDetailType());
				}
				if (isNotBlank(invoiceResponseData.getDetailValue())) {
					response.getStatusOrCreate(0).setConditionDescription(invoiceResponseData.getDetailValue());
				}
				if (isNotBlank(invoiceResponseData.statusReasonText)) {
					response.getStatusOrCreate(0).setStatusReason(invoiceResponseData.statusReasonText);
				}
				if (invoiceResponseData.isEffectiveDateEnabled()) {
					response.setEffectiveDate(invoiceResponseData.getEffectiveDate());
				}
				response.setResponseCode(invoiceResponseData.getStatus());
				response.setResponseCodeListId("UNCL4343OpSubset");
			} else if (data instanceof MessageLevelResponseGenerationData) {
				irData.setProfileID("urn:fdc:peppol.eu:poacc:bis:mlr:3");
				irData.setCustomizationID("urn:fdc:peppol.eu:poacc:trns:mlr:3");
				MessageLevelResponseGenerationData mlrd = (MessageLevelResponseGenerationData) data;
				response.setResponseCode(mlrd.getType());

				if (isNotBlank(mlrd.getDescription())) {
					response.setResponseDescription(mlrd.getDescription());
				}
				if (mlrd.getLineResponseList() != null) {
					lineResponseList = new ArrayList<>();
					for (MessageLevelLineResponse lr : mlrd.getLineResponseList()) {
						Response r = Response.builder().responseCode(lr.getLineCode()).responseDescription(lr.getDescription()).build();
						r.addStatus(ResponseStatus.builder().statusReason(lr.getReasonCode()).build());
						lineResponseList.add(LineResponse.builder().lineId(lr.getLineId()).response(r).build());
					}
				}
			}

			irData.setDocumentResponse(DocumentResponse.builder().response(response).lineResponse(lineResponseList).build());

			try {
				builder.parseAndEnrich(new ByteArrayInputStream(irBytes), irData, out);
				return true;
			} catch (Exception e) {
				log.error("Failed to parse as ApplicationResponse extracted basic data from BIS3 format", e);
				throw new ApplicationResponseGenerationException(document.getId(), "Failed to parse extracted information about sender/receiver as ApplicationResponse from BIS3 format: " + e.getMessage());
			}

		} finally {
			log.info("Finished generation in " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	public List<ErrorRecord> validateMessageLevelResponse(Path xmlFile) {
		return validate(xmlFile, DocumentFormat.BIS3_MESSAGE_LEVEL_RESPONSE);
	}

	public List<ErrorRecord> validateInvoiceResponse(Path xmlFile) {
		return validate(xmlFile, DocumentFormat.BIS3_INVOICE_RESPONSE);
	}

	private List<ErrorRecord> validate(Path xmlFile, DocumentFormat format) {
		Document documentInvoiceResponse = new Document();
		documentInvoiceResponse.setIngoingDocumentFormat(format);

		DocumentProcessLog log = validationTransformationService.process(documentInvoiceResponse, xmlFile, OrganisationReceivingFormatRule.BIS3, null);
		List<DocumentProcessStep> stepList = log.getStepList();
		List<ErrorRecord> errors = new ArrayList<>();
		for (DocumentProcessStep step : stepList) {
			List<ErrorRecord> errorRecords = step.getErrorRecords();
			if (errorRecords != null) {
				errors.addAll(errorRecords);
			}
		}
		return errors;
	}

	public MessageLevelResponseGenerationData buildMLRDataByFailedStep(DocumentProcessStep lastFailedStep) {
		MessageLevelResponseGenerationData d = new MessageLevelResponseGenerationData();
		d.setType("RE");
		if (lastFailedStep == null) {
			d.setDescription("Rejected due to processing errors");
		} else {
			DocumentProcessStepType stepType = lastFailedStep.getStepType();
			int errorCount = 0;
			int warningCount = 0;
			if (lastFailedStep.getErrorRecords() != null) {
				for (ErrorRecord errorRecord : lastFailedStep.getErrorRecords()) {
					if (errorRecord.isWarning()) {
						warningCount++;
					} else {
						errorCount++;
					}
				}
			}

			String errorDescription = "Rejected due to processing errors";

			List<MessageLevelLineResponse> lineResponseList = null;
			if (stepType != null && stepType.isValidation()) {
				errorDescription = "Document is invalid by ";

				if (stepType.isXsd()) {
					errorDescription += "XSD";
				} else {
					errorDescription += "schematron";
				}
				if (errorCount > 0 || warningCount > 0) {
					errorDescription += " with ";
					if (errorCount > 0) {
						errorDescription += errorCount + " errors";
						if (warningCount > 0) {
							errorDescription += " and ";
						}
					}
					if (warningCount > 0) {
						errorDescription += warningCount + " warnings";
					}
				}

				if (lastFailedStep.getDescription() != null) {
					errorDescription += ". " + lastFailedStep.getDescription();
				}

				if (errorCount > 0) {
					lineResponseList = new ArrayList<>();
					List<ErrorRecord> errorRecords = lastFailedStep.getErrorRecords();

					String statusReasonCode = stepType.isXsd() ? "SV" : "BV";
					for (ErrorRecord errorRecord : errorRecords) {
						if (errorRecord.isWarning()) {
							statusReasonCode = "BW";
						}
						MessageLevelLineResponse lr = new MessageLevelLineResponse();
						if (stepType.isXsd()) {
							lr.setLineId("NA");
						} else {
							lr.setLineId(errorRecord.getDetailedLocation());
						}
						lr.setLineCode("RE");

						String description = errorRecord.getMessage();
						if (stepType.isXsd()) {
							description = errorRecord.getDetailedLocation() + ": " + description;
						}
						lr.setDescription(description);
						lr.setReasonCode(statusReasonCode);
						lineResponseList.add(lr);
					}
				}
			}

			d.setDescription(errorDescription);
			d.setLineResponseList(lineResponseList);
		}
		return d;
	}

	@Getter
	public static class ApplicationResponseGenerationException extends Exception {

		private static final long serialVersionUID = 3472994655670634653L;
		private Long documentId;
		private DocumentProcessStep failedStep;

		public ApplicationResponseGenerationException(Long documentId, String message) {
			super(message);
			this.documentId = documentId;
		}

		public ApplicationResponseGenerationException(Long documentId, String message, Throwable cause) {
			super(message, cause);
			this.documentId = documentId;
		}

		public ApplicationResponseGenerationException(Long documentId, String message, DocumentProcessStep failedStep, Throwable cause) {
			super(message, cause);
			this.documentId = documentId;
			this.failedStep = failedStep;
		}
	}
}
