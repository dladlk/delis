package dk.erst.delis.task.document.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.xml.builder.InvoiceResponseBuilder;
import dk.erst.delis.xml.builder.data.DocumentResponse;
import dk.erst.delis.xml.builder.data.InvoiceResponseData;
import dk.erst.delis.xml.builder.data.Response;
import dk.erst.delis.xml.builder.data.ResponseStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceResponseService {

	private DocumentBytesStorageService documentBytesStorageService;

	@Autowired
	public InvoiceResponseService(DocumentBytesStorageService documentBytesStorageService) {
		this.documentBytesStorageService = documentBytesStorageService;
	}
	
	@Data
	public static class InvoiceResponseGenerationData {
		private String status;
		private String action;
		private String reason;
		private String detailType;
		private String detailValue;
	}

	public byte[] generateInvoiceResponse(Document document, InvoiceResponseGenerationData invoiceResponseData) throws InvoiceResponseGenerationException {
		long start = System.currentTimeMillis();
		byte[] res = null;
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
				throw new InvoiceResponseGenerationException(document.getId(),
						"InvoiceResponse can be generated only for ingoing formats CII or BIS3, but current document ingoing format was " + ingoingFamily.getCode());
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

			InvoiceResponseData irData = new InvoiceResponseData();
			irData.setId("IRNumber");
			irData.setIssueDate(dateFormat.format(currentTime));
			irData.setIssueTime(timeFormat.format(currentTime));
			
			ResponseStatus responseStatus = ResponseStatus.builder().build();
			responseStatus.setStatusReasonCode(invoiceResponseData.getAction());
			responseStatus.setStatusReason(invoiceResponseData.getReason());
			responseStatus.setConditionAttributeID(invoiceResponseData.getDetailType());
			responseStatus.setConditionDescription(invoiceResponseData.getDetailValue());
			
			Response response = Response.builder().build();
			response.setEffectiveDate(dateFormat.format(currentTime));
			response.setResponseCode("AB");
			response.setStatus(responseStatus);
			irData.setDocumentResponse(DocumentResponse.builder().response(response).build());

			ByteArrayOutputStream resOutput = new ByteArrayOutputStream();
			try {
				b.parseAndEnrich(new ByteArrayInputStream(irBytes), irData, resOutput);
			} catch (Exception e) {
				log.error("Failed to parse as ApplicationResponse extracted basic data from BIS3 format", e);
				throw new InvoiceResponseGenerationException(document.getId(),
						"Failed to parse extracted information about sender/receiver as ApplicationResponse from BIS3 format: " + e.getMessage());
			}

			res = resOutput.toByteArray();
		} finally {
			log.info("Finished generation in " + (System.currentTimeMillis() - start) + " ms with result " + (res == null ? "null" : String.valueOf(res.length)) + " bytes");
		}

		return res;
	}

	public static class InvoiceResponseGenerationException extends Exception {

		private static final long serialVersionUID = 3472994655670634653L;
		private Long documentId;

		public InvoiceResponseGenerationException(Long documentId, String message) {
			super(message);
			this.documentId = documentId;
		}

		public Long getDocumentId() {
			return documentId;
		}
	}
}
