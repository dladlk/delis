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
import dk.erst.delis.task.document.response.ApplicationResponseService;
import dk.erst.delis.task.document.response.ApplicationResponseService.ApplicationResponseGenerationData;
import dk.erst.delis.task.document.response.ApplicationResponseService.ApplicationResponseGenerationException;
import dk.erst.delis.task.document.response.ApplicationResponseService.InvoiceResponseGenerationData;
import dk.erst.delis.task.document.response.ApplicationResponseService.MessageLevelResponseGenerationData;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ApplicationResponseFormController {

	@Autowired
	private SendDocumentService sendDocumentService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private ApplicationResponseService applicationResponseService;
	@Autowired
	private DocumentProcessService documentProcessService;
	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@Getter
	@Setter
	public static abstract class AbstractApplicationResponseForm {
		private long documentId;
		private String usecase;
		private boolean generateWithoutSending = true;
		private boolean validate = true;

		public abstract boolean isMessageLevelResponse();

		public abstract String getDocumentFormatName();

		public abstract ApplicationResponseGenerationData getData();
	}

	@Getter
	@Setter
	public static class InvoiceResponseForm extends AbstractApplicationResponseForm {
		@Delegate
		@Getter
		private InvoiceResponseGenerationData data = new InvoiceResponseGenerationData();

		@Override
		public boolean isMessageLevelResponse() {
			return false;
		}

		@Override
		public String getDocumentFormatName() {
			return "InvoiceResponse";
		}
	}

	@Getter
	@Setter
	public static class MessageLevelResponseForm extends AbstractApplicationResponseForm {
		@Delegate
		@Getter
		private MessageLevelResponseGenerationData data = new MessageLevelResponseGenerationData();

		@Override
		public boolean isMessageLevelResponse() {
			return true;
		}

		@Override
		public String getDocumentFormatName() {
			return "MessageLevelResponse";
		}
	}

	@PostMapping("/document/generate/messageLevelResponseByErrorAndSend/{id}")
	public String generateMessageLevelResponseByLastErrorAndSend(@PathVariable long id, Model model, RedirectAttributes ra) throws IOException {
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
			ra.addFlashAttribute("responseErrorList", step.getErrorRecords());
		}

		return "redirect:/document/view/" + id;
	}

	@PostMapping("/document/generate/invoiceResponse")
	public ResponseEntity<Object> generateInvoiceResponse(InvoiceResponseForm irForm, RedirectAttributes ra) throws IOException {
		ra.addFlashAttribute("irForm", irForm);
		return generateApplicationResponse(irForm, ra);
	}

	@PostMapping("/document/generate/messageLevelResponse")
	public ResponseEntity<Object> generateMessageLevelResponse(MessageLevelResponseForm mlrForm, RedirectAttributes ra) throws IOException {
		ra.addFlashAttribute("mlrForm", mlrForm);
		return generateApplicationResponse(mlrForm, ra);
	}

	private ResponseEntity<Object> generateApplicationResponse(AbstractApplicationResponseForm arForm, RedirectAttributes ra) throws IOException {
		log.info("Generating ApplicationResponse for " + arForm);

		Document document = documentService.getDocument(arForm.getDocumentId());
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return redirectEntity(servletContextPath, "/home");
		}

		String defaultReturnPath = "/document/view/" + arForm.getDocumentId();
		boolean success = false;
		File tempFile = Files.createTempFile("Generated" + arForm.getDocumentFormatName() + "_", ".xml").toFile();
		try (OutputStream out = new FileOutputStream(tempFile)) {
			success = applicationResponseService.generateApplicationResponse(document, arForm.getData(), out);
		} catch (ApplicationResponseGenerationException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			if (e.getDocumentId() != null) {
				return redirectEntity(servletContextPath, defaultReturnPath);
			}
			return redirectEntity(servletContextPath, "/home");
		}

		if (!success) {
			ra.addFlashAttribute("errorMessage", "Failed to generate " + arForm.getDocumentFormatName());
			return redirectEntity(servletContextPath, defaultReturnPath);
		}

		if (arForm.isValidate()) {
			List<ErrorRecord> errorList;
			if (arForm.isMessageLevelResponse()) {
				errorList = applicationResponseService.validateMessageLevelResponse(tempFile.toPath());
			} else {
				errorList = applicationResponseService.validateInvoiceResponse(tempFile.toPath());
			}
			if (!errorList.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Generated ");
				sb.append(arForm.getDocumentFormatName());
				sb.append(" is not valid by schema or schematron, found ");
				sb.append(errorList.size());
				sb.append(" errors");
				ra.addFlashAttribute("errorMessage", sb.toString());
				if (arForm.isMessageLevelResponse()) {
					ra.addFlashAttribute("messageLevelResponseFormOpened", true);
				} else {
					ra.addFlashAttribute("invoiceResponseFormOpened", true);
				}
				ra.addFlashAttribute("responseErrorList", errorList);
				return redirectEntity(servletContextPath, defaultReturnPath);
			}
		}

		if (arForm.isGenerateWithoutSending()) {
			BodyBuilder resp = ResponseEntity.ok();
			resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arForm.getDocumentFormatName() + ".xml\"");
			resp.contentType(MediaType.parseMediaType("application/octet-stream"));
			return resp.body(new InputStreamResource(new FileInputStream(tempFile)));
		}

		try {
			SendDocument sendDocument = sendDocumentService.sendFile(tempFile.toPath(), "Generated by form on document #" + arForm.getDocumentId(), !arForm.isValidate());
			ra.addFlashAttribute("message", "Successfully sent generated " + arForm.getDocumentFormatName() + " with status " + sendDocument.getDocumentStatus());
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

	public static void fillModel(Model model, Document document) {
		if (!model.containsAttribute("irForm")) {
			InvoiceResponseForm irForm = new InvoiceResponseForm();
			irForm.setDocumentId(document.getId());
			model.addAttribute("irForm", irForm);
		}
		if (!model.containsAttribute("mlrForm")) {
			MessageLevelResponseForm mlrForm = new MessageLevelResponseForm();
			mlrForm.setDocumentId(document.getId());
			model.addAttribute("mlrForm", mlrForm);
		}

		/*
		 * Invoice Response form
		 */
		model.addAttribute("invoiceResponseUseCaseList", InvoiceResponseFormControllerConst.useCaseList);
		model.addAttribute("invoiceStatusCodeList", InvoiceResponseFormControllerConst.invoiceStatusCodeList);
		model.addAttribute("statusActionList", InvoiceResponseFormControllerConst.statusActionList);
		model.addAttribute("statusReasonList", InvoiceResponseFormControllerConst.statusReasonList);
		/*
		 * Message Level Response
		 */
		model.addAttribute("messageLevelResponseUseCaseList", MessageLevelResponseConst.useCaseList);
		model.addAttribute("applicationResponseTypeCodeList", MessageLevelResponseConst.applicationResponseTypeCodeList);
	}

}
