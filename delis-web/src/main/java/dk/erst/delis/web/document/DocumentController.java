package dk.erst.delis.web.document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import dk.erst.delis.pagefiltering.response.PageContainer;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
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

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.InvoiceResponseService;
import dk.erst.delis.task.document.response.InvoiceResponseService.InvoiceResponseGenerationData;
import dk.erst.delis.task.document.response.InvoiceResponseService.InvoiceResponseGenerationException;
import lombok.Getter;
import lombok.Setter;
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
	private DocumentBytesDaoRepository documentBytesDaoRepository;
	@Autowired
	private InvoiceResponseService invoiceResponseService;
	
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

	@RequestMapping("/document/list")
	public String list(Model model, WebRequest webRequest) {
		return listFilter(model, webRequest);
	}

	@PostMapping("/document/list/filter")
	public String listFilter(Model model, WebRequest webRequest) {
		PageContainer<Document> pageContainer = documentService.getAll(webRequest);
		model.addAttribute("documentList", pageContainer);
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
		InvoiceResponseForm irForm = new InvoiceResponseForm();
		irForm.setDocumentId(document.getId());
		model.addAttribute("irForm", irForm);

		return "/document/view";
	}
	
	private ResponseEntity<Object> redirectEntity(String url) {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    try {
			httpHeaders.setLocation(new URI(this.servletContextPath + url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
	}

	@PostMapping("/document/generate/invoiceResponse")
	public ResponseEntity<Object> generateInvoiceResponse(InvoiceResponseForm irForm, RedirectAttributes ra) {
		log.info("Generating InvoiceResponse for " + irForm);
		
		Document document = documentService.getDocument(irForm.getDocumentId());
		if (document == null) {
			ra.addFlashAttribute("errorMessage", "Document is not found");
			return redirectEntity("/home");
		}
		
		byte[] res;
		String defaultReturnPath = "/document/view/" + irForm.getDocumentId();
		try {
			res = invoiceResponseService.generateInvoiceResponse(document, irForm);
		} catch (InvoiceResponseGenerationException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
			if (e.getDocumentId() != null) {
				return redirectEntity(defaultReturnPath);
			}
			return redirectEntity("/home");
		}
		
		if (res == null) {
			ra.addFlashAttribute("errorMessage", "Failed to generate InvoiceResponse");
			return redirectEntity(defaultReturnPath);
		}
		
		Path tempFile = null;
		try {
			tempFile = Files.createTempFile("GeneratedInvoiceResponse_", ".xml");
			FileUtils.copyToFile(new ByteArrayInputStream(res), tempFile.toFile());
		} catch (IOException e) {
			log.error("Failed to save generated invoice response to file "+tempFile, e);
			ra.addFlashAttribute("errorMessage", "Failed to save generated InvoiceResponse");
			return redirectEntity(defaultReturnPath);
		}
		
		List<ErrorRecord> errorList = invoiceResponseService.validateInvoiceResponse(tempFile);
		if (!errorList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Generated InvoiceResponse is not valid by schema or schematron, found ");
			sb.append(errorList.size());
			sb.append(" errors");
			ra.addFlashAttribute("errorMessage", sb.toString());
			ra.addFlashAttribute("invoiceResponseErrorList", errorList);
			return redirectEntity(defaultReturnPath);
		}
		
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"InvoiceResponse.xml\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(res);
	}
	
	@Getter @Setter
	public static class InvoiceResponseForm extends InvoiceResponseGenerationData {
		private long documentId;
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
