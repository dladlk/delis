package dk.erst.delis.web.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import dk.erst.delis.task.document.DocumentBytesService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DocumentController {

	@Autowired
	private DocumentProcessService documentProcessService;
	@Autowired
	private DocumentLoadService documentLoadService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private DocumentBytesService documentBytesService;

	@RequestMapping("/document/list")
	public String list(Model model) {
		return listFilter(model);
	}

	@PostMapping("/document/list/filter")
	public String listFilter(Model model) {
		List<Document> list = documentService.documentList(0, 10);
		model.addAttribute("documentList", list);
		model.addAttribute("selectedIdList", new DocumentStatusBachUdpateInfo());
		model.addAttribute("statusList", DocumentStatus.values());
		return "/document/list";
	}

	@PostMapping("/document/updatestatuses")
	public String listFilter(@ModelAttribute DocumentStatusBachUdpateInfo idList, Model model) {
		List<Long> ids = idList.getIdList();
		DocumentStatus status = idList.getStatus();
		documentService.updateStatuses(ids, status);
		return "redirect:/document/list";
	}

	@PostMapping("/document/updatestatus")
	public String updateStatus(Document staleDocument, RedirectAttributes ra) {
		Long id = staleDocument.getId();
		DocumentStatus documentStatus = staleDocument.getDocumentStatus();
		int count = documentService.updateStatus(id, documentStatus);
		if (count == 0) {
			ra.addFlashAttribute("errorMessage", "Document with ID " + id + " is not found");
			return "redirect:/document/list";
		}
		return "redirect:/document/view/" + id;
	}

	@GetMapping("/document/view/{id}")
	public String view(@PathVariable long id, Model model, RedirectAttributes ra) {
		Document document = documentService.getDocument(id);
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return "redirect:/home";
		}

		model.addAttribute("document", document);
		model.addAttribute("documentStatusList", DocumentStatus.values());
		model.addAttribute("lastJournalList", documentService.getDocumentRecords(document));
		model.addAttribute("documentBytes", documentBytesService.findAllDocumentBytes(document));

		return "/document/view";
	}

	@PostMapping("/document/upload")
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
				Document document = documentLoadService.loadFile(tempFile.toPath());
				if (document == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "Cannot load file " + tempFile + " as document, see logs");
				} else {
					if (document.getDocumentStatus().isLoadFailed()) {
						redirectAttributes.addFlashAttribute("errorMessage", "Uploaded file as a document with status " + document.getDocumentStatus());
					} else {
						if (validateImmediately) {
							StatData statData = new StatData();
							documentProcessService.processDocument(statData, document);
							redirectAttributes.addFlashAttribute("message", "Successfully uploaded file and validated: " + statData.toStatString());
						} else {
							redirectAttributes.addFlashAttribute("message", "Successfully uploaded file as a document with status " + document.getDocumentStatus());
						}
					}
					return "redirect:/document/view/" + document.getId();
				}
			}
		}

		return "redirect:/document/list";
	}

}
