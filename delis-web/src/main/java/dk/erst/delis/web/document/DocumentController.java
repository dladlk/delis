package dk.erst.delis.web.document;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.document.ir.ApplicationResponseFormController;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.List;

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
	private DocumentBytesDaoRepository documentBytesDaoRepository;
	@Autowired
	private ApplicationResponseFormController applicationResponseFormController;
	
    @Value("${servletContext?.contextPath:}")
    private String servletContextPath;
    
	@Value("${delis.download.allow.all:#{false}}")
	private boolean downloadAllowAll;    

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
		model.addAttribute("errorListByJournalDocumentIdMap", documentService.getErrorListByJournalDocumentIdMap(document));
		model.addAttribute("documentBytes", documentBytesDaoRepository.findByDocument(document));
		
		applicationResponseFormController.fillModel(model, document);

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

	private boolean isDownloadAllowed(DocumentBytes b) {
		if (downloadAllowAll) {
			return true;
		}
		return b.getType() == DocumentBytesType.IN_AS4;
	}

	@GetMapping("/document/download/{documentId}/{bytesId}")
	public ResponseEntity<Object> download(@PathVariable long documentId, @PathVariable long bytesId, RedirectAttributes ra) throws IOException {
		DocumentBytes documentBytes = documentService.findDocumentBytes(documentId, bytesId);
		if (documentBytes == null) {
			ra.addFlashAttribute("errorMessage", "Data not found");
			return RedirectUtil.redirectEntity(servletContextPath, "/document/view/" + documentId);
		}
		if (!isDownloadAllowed(documentBytes)) {
			ra.addFlashAttribute("errorMessage", "Only RECEIPT bytes are allowed for download, but " + documentBytes.getType() + " is requested");
			return RedirectUtil.redirectEntity(servletContextPath, "/document/view/" + documentId);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.documentService.getDocumentBytesContents(documentBytes, out);
		byte[] data = out.toByteArray();
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data_" + documentId + "_" + bytesId + ".xml\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
	}
}
