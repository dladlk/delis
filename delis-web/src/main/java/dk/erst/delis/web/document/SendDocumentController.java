package dk.erst.delis.web.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.pagefiltering.response.PageContainer;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SendDocumentController {

	@Autowired
	@Qualifier("sendDocumentService")
	private SendDocumentService documentService;
	
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

	@RequestMapping("/document/send/list")
	public String list(Model model, WebRequest webRequest) {
		return listFilter(model, webRequest);
	}

	@PostMapping("/document/send/list/filter")
	public String listFilter(Model model, WebRequest webRequest) {
		PageContainer<SendDocument> pageContainer = documentService.getAll(webRequest);
		model.addAttribute("documentList", pageContainer);
		model.addAttribute("selectedIdList", new SendDocumentStatusBachUdpateInfo());
		model.addAttribute("statusList", SendDocumentStatus.values());
		return "/document/send/list";
	}

	@PostMapping("/document/send/updatestatuses")
	public String listFilter(@ModelAttribute SendDocumentStatusBachUdpateInfo idList, Model model) {
		List<Long> ids = idList.getIdList();
		SendDocumentStatus status = idList.getStatus();
		documentService.updateStatuses(ids, status);
		return "redirect:/document/send/list";
	}

	@PostMapping("/document/send/updatestatus")
	public String updateStatus(SendDocument document, RedirectAttributes ra) {
		Long id = document.getId();
		SendDocumentStatus documentStatus = document.getDocumentStatus();
		int count = documentService.updateStatus(id, documentStatus);
		if (count == 0) {
			ra.addFlashAttribute("errorMessage", "Document with ID " + id + " is not found");
			return "redirect:/document/send/list";
		}
		return "redirect:/document/send/view/" + id;
	}

	@GetMapping("/document/send/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		SendDocument document = documentService.getDocument(id);
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return "redirect:/home";
		}

		model.addAttribute("document", document);
		model.addAttribute("documentStatusList", SendDocumentStatus.values());
		model.addAttribute("documentBytes", documentService.getDocumentBytes(document));
		model.addAttribute("lastJournalList", documentService.getDocumentRecords(document));

		return "/document/send/view";
	}
	
	@PostMapping("/document/send/upload")
	public String upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam(name = "validateImmediately", required = false) boolean validateImmediately,
			RedirectAttributes redirectAttributes) {

		if (file == null || file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "File is empty");
		} else {

			File tempFile = null;
			try {
				tempFile = File.createTempFile("manual_upload_" + file.getName() + "_", ".xml");
				try (FileOutputStream fos = new FileOutputStream(tempFile)) {
					IOUtils.copy(file.getInputStream(), fos);
				}
			} catch (IOException e) {
				log.error("Failed to save uploaded file to temp for " + file.getName(), e);
			}
			if (tempFile != null) {
				log.info("Created test file " + tempFile);
				try {
					SendDocument document = documentService.sendFile(tempFile.toPath(), "Uploaded manually " + file.getName(),validateImmediately);
					redirectAttributes.addFlashAttribute("message", "Successfully uploaded file as a document with status " + document.getDocumentStatus());
					return "redirect:/document/send/view/" + document.getId();
				} catch (DocumentProcessStepException se) {
					log.error("Failed document processing", se);
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to process file " + tempFile + " with error "+se.getMessage());
					if (se.getDocumentId() != null) {
						return "redirect:/document/send/view/" + se.getDocumentId();
					}

				} catch (Exception e) {
					log.error("Failed to load file "+tempFile, e);
					redirectAttributes.addFlashAttribute("errorMessage", "Failed to load file " + tempFile + " with error "+e.getMessage());
				}
			}
		}

		return "redirect:/document/send/list";
	}

}
