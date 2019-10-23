package dk.erst.delis.web.document.ir;

import static dk.erst.delis.web.RedirectUtil.redirectEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.ApplicationResponseService;
import dk.erst.delis.task.document.response.ApplicationResponseService.ApplicationResponseGenerationException;
import dk.erst.delis.task.document.response.ApplicationResponseService.MessageLevelResponseGenerationData;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.SendDocumentService;
import dk.erst.delis.web.email.EmailSendService;
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
	@Autowired
	private EmailResponseService emailResponseService;
	@Autowired 
	private EmailSendService emailSendService;

	@PostMapping("/document/generate/messageLevelResponseByErrorAndSend/{id}")
	public String generateMessageLevelResponseByLastErrorAndSend(@PathVariable long id, Model model, RedirectAttributes ra) throws IOException {
		Document document = documentService.getDocument(id);
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return "redirect:/home";
		}

		/*
		 * TODO: Load last failed step from database
		 */
		DocumentProcessStep lastFailedStep = null;
		DocumentProcessStep step = documentProcessService.generateAndSendMessageLevelResponse(document, lastFailedStep);
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

	@PostMapping("/document/generate/emailResponse")
	public ResponseEntity<Object> generateEmailResponse(EmailResponseForm emailForm, RedirectAttributes ra) throws IOException {
		String defaultReturnPath = "/document/view/" + emailForm.getDocumentId();
		
		ra.addFlashAttribute("emailForm", emailForm);
		
		if (emailForm.isValid()) {
			if (emailSendService.send(emailForm)) {
				ra.addFlashAttribute("message", "Email is successfully sent");
			} else {
				ra.addFlashAttribute("errorMessage", "Email sending failed");
			}
		} else {
			ra.addFlashAttribute("errorMessage", "Email delivery is not yet implemented");
		}

		return redirectEntity(defaultReturnPath);
	}

	private ResponseEntity<Object> generateApplicationResponse(AbstractApplicationResponseForm arForm, RedirectAttributes ra) throws IOException {
		log.info("Generating ApplicationResponse for " + arForm);

		Document document = documentService.getDocument(arForm.getDocumentId());
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return redirectEntity("/home");
		}

		String defaultReturnPath = "/document/view/" + arForm.getDocumentId();
		boolean success = false;
		File tempFile = Files.createTempFile("Generated" + arForm.getDocumentFormatName() + "_", ".xml").toFile();
		try (OutputStream out = new FileOutputStream(tempFile)) {
			success = applicationResponseService.generateApplicationResponse(document, arForm.getData(), out);
		} catch (ApplicationResponseGenerationException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			if (e.getDocumentId() != null) {
				return redirectEntity(defaultReturnPath);
			}
			return redirectEntity("/home");
		}

		if (!success) {
			ra.addFlashAttribute("errorMessage", "Failed to generate " + arForm.getDocumentFormatName());
			return redirectEntity(defaultReturnPath);
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
				return redirectEntity(defaultReturnPath);
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
				return redirectEntity("redirect:/document/send/view/" + se.getDocumentId());
			}
		} catch (Exception e) {
			log.error("Failed to load file " + tempFile, e);
			ra.addFlashAttribute("errorMessage", "Failed to load file " + tempFile + " with error " + e.getMessage());
		}
		return redirectEntity(defaultReturnPath);
	}

	@SuppressWarnings("unchecked")
	public void fillModel(Model model, Document document) {
		if (!model.containsAttribute("irForm")) {
			InvoiceResponseForm irForm = new InvoiceResponseForm();
			irForm.setDocumentId(document.getId());
			irForm.setEffectiveDate(document.getDocumentDate());
			model.addAttribute("irForm", irForm);
		}
		MessageLevelResponseForm mlrForm;
		if (!model.containsAttribute("mlrForm")) {
			mlrForm = new MessageLevelResponseForm();
			mlrForm.setDocumentId(document.getId());
			model.addAttribute("mlrForm", mlrForm);

			if (model.containsAttribute("lastJournalList")) {
				List<JournalDocument> lastJournalList = (List<JournalDocument>) model.asMap().get("lastJournalList");

				JournalDocument lastFailedValidationJournal = null;
				for (int i = lastJournalList.size() - 1; i >= 0; i--) {
					JournalDocument journalDocument = lastJournalList.get(i);
					if (!journalDocument.isSuccess() && journalDocument.getType().isValidation()) {
						lastFailedValidationJournal = journalDocument;
						break;
					}
				}
				if (lastFailedValidationJournal != null) {
					if (model.containsAttribute("errorListByJournalDocumentIdMap")) {
						Map<Long, List<ErrorDictionary>> errorListByJournalDocumentIdMap = (Map<Long, List<ErrorDictionary>>) model.asMap().get("errorListByJournalDocumentIdMap");
						List<ErrorDictionary> list = errorListByJournalDocumentIdMap.get(lastFailedValidationJournal.getId());
						if (list != null && !list.isEmpty()) {
							DocumentProcessStep s = new DocumentProcessStep(lastFailedValidationJournal.getMessage(), lastFailedValidationJournal.getType());
							s.setSuccess(false);

							List<ErrorRecord> errorRecords = new ArrayList<ErrorRecord>();
							for (ErrorDictionary ed : list) {
								ErrorRecord e = new ErrorRecord(ed.getErrorType(), ed.getCode(), ed.getMessage(), ed.getFlag(), ed.getLocation());
								e.setDetailedLocation(ed.getLocation());
								errorRecords.add(e);
							}
							s.setErrorRecords(errorRecords);

							MessageLevelResponseGenerationData mlrData = this.applicationResponseService.buildMLRDataByFailedStep(s);
							mlrForm.setData(mlrData);
						}
					}
				}
			}
		} else {
			mlrForm	= (MessageLevelResponseForm) model.asMap().get("mlrForm");
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
		model.addAttribute("applicationResponseLineResponseCodeList", MessageLevelResponseConst.applicationResponseLineResponseCodeList);
		model.addAttribute("applicationResponseLineReasonCodeList", MessageLevelResponseConst.applicationResponseLineReasonCodeList);
		
		if (!model.containsAttribute("emailForm")) {
			model.addAttribute("emailForm", emailResponseService.buildEmailResponse(document, mlrForm));
		}
	}

}
