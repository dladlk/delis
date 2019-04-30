package dk.erst.delis.web.document.ir;

import static dk.erst.delis.web.RedirectUtil.redirectEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.InvoiceResponseService;
import dk.erst.delis.task.document.response.InvoiceResponseService.InvoiceResponseGenerationData;
import dk.erst.delis.task.document.response.InvoiceResponseService.InvoiceResponseGenerationException;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class InvoiceResponseFormController {

	@Autowired
	private SendDocumentService sendDocumentService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private InvoiceResponseService invoiceResponseService;
	@Autowired
	private DocumentProcessService documentProcessService;
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;
	
	@Getter
	@Setter
	public static class InvoiceResponseForm extends InvoiceResponseGenerationData {
		private long documentId;
		private String usecase;
		private boolean generateWithoutSending = true;
		private boolean validate = true;
	}

	@PostMapping("/document/generate/invoiceResponseByErrorAndSend/{id}")
	public String generateInvoiceResponseByLastErrorAndSend(@PathVariable long id, Model model, RedirectAttributes ra) throws IOException {
		Document document = documentService.getDocument(id);
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return "redirect:/home";
		}

		DocumentProcessStep step = documentProcessService.generateAndSendInvoiceResponse(document);
		if (step.isSuccess()) {
			ra.addFlashAttribute("message", step.getMessage());
		} else {
			ra.addFlashAttribute("errorMessage", step.getMessage());
			ra.addFlashAttribute("invoiceResponseErrorList", step.getErrorRecords());
		}

		return "redirect:/document/view/" + id;
	}

	@PostMapping("/document/generate/invoiceResponse")
	public ResponseEntity<Object> generateInvoiceResponse(InvoiceResponseForm irForm, RedirectAttributes ra) throws IOException {
		log.info("Generating InvoiceResponse for " + irForm);
		ra.addFlashAttribute("irForm", irForm);

		Document document = documentService.getDocument(irForm.getDocumentId());
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return redirectEntity(servletContextPath, "/home");
		}

		String defaultReturnPath = "/document/view/" + irForm.getDocumentId();
		boolean success = false;
		File tempFile = Files.createTempFile("GeneratedInvoiceResponse_", ".xml").toFile();
		try (OutputStream out = new FileOutputStream(tempFile)) {
			success = invoiceResponseService.generateInvoiceResponse(document, irForm, out);
		} catch (InvoiceResponseGenerationException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			if (e.getDocumentId() != null) {
				return redirectEntity(servletContextPath, defaultReturnPath);
			}
			return redirectEntity(servletContextPath, "/home");
		}

		if (!success) {
			ra.addFlashAttribute("errorMessage", "Failed to generate InvoiceResponse");
			return redirectEntity(servletContextPath, defaultReturnPath);
		}

		if (irForm.isValidate()) {
			List<ErrorRecord> errorList = invoiceResponseService.validateInvoiceResponse(tempFile.toPath());
			if (!errorList.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Generated InvoiceResponse is not valid by schema or schematron, found ");
				sb.append(errorList.size());
				sb.append(" errors");
				ra.addFlashAttribute("errorMessage", sb.toString());
				ra.addFlashAttribute("invoiceResponseErrorList", errorList);
				return redirectEntity(servletContextPath, defaultReturnPath);
			}
		}

		if (irForm.isGenerateWithoutSending()) {
			BodyBuilder resp = ResponseEntity.ok();
			resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"InvoiceResponse.xml\"");
			resp.contentType(MediaType.parseMediaType("application/octet-stream"));
			return resp.body(new InputStreamResource(new FileInputStream(tempFile)));
		}

		try {
			SendDocument sendDocument = sendDocumentService.sendFile(tempFile.toPath(), "Generated by form on document #" + irForm.getDocumentId(), !irForm.isValidate());
			ra.addFlashAttribute("message", "Successfully sent generated InvoiceResponse with status " + sendDocument.getDocumentStatus());
		} catch (DocumentProcessStepException se) {
			log.error("Failed document processing", se);
			ra.addFlashAttribute("errorMessage", "Failed to process file " + tempFile + " with error " + se.getMessage());
			if (se.getDocumentId() != null) {
				return redirectEntity(servletContextPath, "redirect:/document/send/view/" + se.getDocumentId());
			}
		} catch (Exception e) {
			log.error("Failed to load file " + tempFile, e);
			ra.addFlashAttribute("errorMessage", "Failed to load file " + tempFile + " with error " + e.getMessage());
		}
		return redirectEntity(servletContextPath, defaultReturnPath);
	}

}
