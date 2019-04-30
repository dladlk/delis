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
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.xml.builder.InvoiceResponseBuilder;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.InvoiceResponseData;
import dk.erst.delis.xml.builder.data.Response;
import dk.erst.delis.xml.builder.data.ResponseStatus;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceResponseService {

	private DocumentBytesStorageService documentBytesStorageService;
	private DocumentValidationTransformationService validationTransformationService;

	@Autowired
	public InvoiceResponseService(DocumentBytesStorageService documentBytesStorageService, DocumentValidationTransformationService validationTransformationService) {
		this.documentBytesStorageService = documentBytesStorageService;
		this.validationTransformationService = validationTransformationService;
	}

	@Data
	public static class InvoiceResponseGenerationData {
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
	}

	public boolean generateInvoiceResponse(Document document, InvoiceResponseGenerationData invoiceResponseData, OutputStream out) throws InvoiceResponseGenerationException {
		long start = System.currentTimeMillis();
		try {
			log.info("Started InvoiceResponse generation for document " + document);
			DocumentBytesType documentBytesType = null;
			DocumentFormatFamily ingoingFamily = document.getIngoingDocumentFormat().getDocumentFormatFamily();
			if (ingoingFamily == DocumentFormatFamily.BIS3) {
				documentBytesType = DocumentBytesType.IN;
			} else if (ingoingFamily == DocumentFormatFamily.CII) {
				documentBytesType = DocumentBytesType.INTERM;
			}

			log.info("Search for document bytes type " + documentBytesType);

			if (documentBytesType == null) {
				throw new InvoiceResponseGenerationException(document.getId(), "InvoiceResponse can be generated only for ingoing formats CII or BIS3, but current document ingoing format was " + ingoingFamily.getCode());
			}

			DocumentBytes documentBytes = documentBytesStorageService.find(document, documentBytesType);
			log.info("Found document bytes: " + documentBytes);
			if (documentBytes == null) {
				throw new InvoiceResponseGenerationException(document.getId(), "Cannot find document data in format BIS3 by bytes type " + documentBytesType.getCode());
			}

			ByteArrayOutputStream bisOutput = new ByteArrayOutputStream();
			documentBytesStorageService.load(documentBytes, bisOutput);

			byte[] xmlBytes = bisOutput.toByteArray();
			log.info("Loaded " + xmlBytes.length + " bytes");

			InvoiceResponseBuilder b = new InvoiceResponseBuilder();

			ByteArrayOutputStream irOutput = new ByteArrayOutputStream();
			try {
				b.extractBasicData(new ByteArrayInputStream(xmlBytes), irOutput);
			} catch (Exception e) {
				log.error("Failed to extract basic data from BIS3 format", e);
				throw new InvoiceResponseGenerationException(document.getId(), "Failed to extract information about sender/receiver from BIS3 format: " + e.getMessage());
			}

			byte[] irBytes = irOutput.toByteArray();

			Date currentTime = Calendar.getInstance().getTime();
			SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

			InvoiceResponseData irData = new InvoiceResponseData();
			irData.setIssueDate(dateFormat.format(currentTime));
			irData.setIssueTime(timeFormat.format(currentTime));
			irData.setId(idFormat.format(currentTime) + "-" + document.getId());

			Response response = Response.builder().build();

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

			response.setEffectiveDate(dateFormat.format(currentTime));
			response.setResponseCode(invoiceResponseData.getStatus());
			response.setResponseCodeListId("UNCL4343OpSubset");
			irData.setDocumentResponse(DocumentResponse.builder().response(response).build());

			try {
				b.parseAndEnrich(new ByteArrayInputStream(irBytes), irData, out);
				return true;
			} catch (Exception e) {
				log.error("Failed to parse as ApplicationResponse extracted basic data from BIS3 format", e);
				throw new InvoiceResponseGenerationException(document.getId(), "Failed to parse extracted information about sender/receiver as ApplicationResponse from BIS3 format: " + e.getMessage());
			}

		} finally {
			log.info("Finished generation in " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	public List<ErrorRecord> validateInvoiceResponse(Path xmlFile) {
		Document documentInvoiceResponse = new Document();
		documentInvoiceResponse.setIngoingDocumentFormat(DocumentFormat.BIS3_INVOICE_RESPONSE);

		DocumentProcessLog log = validationTransformationService.process(documentInvoiceResponse, xmlFile, OrganisationReceivingFormatRule.BIS3);
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

	@Getter
	public static class InvoiceResponseGenerationException extends Exception {

		private static final long serialVersionUID = 3472994655670634653L;
		private Long documentId;
		private DocumentProcessStep failedStep;

		public InvoiceResponseGenerationException(Long documentId, String message) {
			super(message);
			this.documentId = documentId;
		}

		public InvoiceResponseGenerationException(Long documentId, String message, Throwable cause) {
			super(message, cause);
			this.documentId = documentId;
		}

		public InvoiceResponseGenerationException(Long documentId, String message, DocumentProcessStep failedStep, Throwable cause) {
			super(message, cause);
			this.documentId = documentId;
			this.failedStep = failedStep;
		}
	}
}
