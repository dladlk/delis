package dk.erst.delis.web.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.Authentication;
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

import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.dao.SendDocumentDataTableRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.container.ColumnDefs;
import dk.erst.delis.web.container.PageDataContainer;
import dk.erst.delis.web.util.WebRequestUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SendDocumentController {

	@Autowired
	@Qualifier("sendDocumentService")
	private SendDocumentService documentService;

	@Autowired
	private SendDocumentDaoRepository sendDocumentDaoRepository;

	@Autowired
	private SendDocumentDataTableRepository sendDocumentDataTableRepository;

	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@Value("${delis.download.allow.all:#{false}}")
	private boolean downloadAllowAll;

	@RequestMapping("/document/send/list")
	public String list(Model model, WebRequest webRequest) {

		PageDataContainer pageDataContainer = WebRequestUtil.getPageDataContainerByWebRequest(webRequest);
		if (pageDataContainer == null) {
			pageDataContainer = new PageDataContainer();
		}
		
		String[] orderBy = WebRequestUtil.orderParam(webRequest);
		String sortBy = "id";
		List<String> documentColumns = Arrays.asList("id", "createTime", "organisation.id", "senderIdRaw", "receiverIdRaw", "documentStatus", "documentType", "deliveredTime");
		for (int i = 0; i < documentColumns.size(); i++) {
			if (i == Integer.parseInt(orderBy[0])) {
				sortBy = documentColumns.get(i);
				break;
			}
		}
		
		Page<SendDocument> sendDocuments;
		if (pageDataContainer.getOrderDir().equals(orderBy[1])) {
			sendDocuments = sendDocumentDaoRepository
					.findAll(PageRequest.of(pageDataContainer.getPage() - 1, pageDataContainer.getSize(), Sort.by(sortBy).descending()));
		} else {
			sendDocuments = sendDocumentDaoRepository
					.findAll(PageRequest.of(pageDataContainer.getPage() - 1, pageDataContainer.getSize(), Sort.by(sortBy).ascending()));
		}
		 
		if (pageDataContainer.getPage() > sendDocuments.getTotalPages()) {
			pageDataContainer.setPage(pageDataContainer.getPage() - 1);
		}
		
		pageDataContainer.setTotalElements(sendDocumentDaoRepository.count());
		pageDataContainer.setTotalPages(sendDocuments.getTotalPages());
		int[] targets = new int[] { 0, 5, 6 };
		pageDataContainer.setColumnDefs(new ColumnDefs(targets, false));
		pageDataContainer.setOrderCol(1);
		pageDataContainer.setOrderDir("desc");

		model.addAttribute("sendDocumentsList", sendDocuments.getContent());
		model.addAttribute("pageDataContainer", pageDataContainer);
		model.addAttribute("selectedIdList", new SendDocumentStatusBachUdpateInfo());
		model.addAttribute("statusList", SendDocumentStatus.values());

		return "/document/send/list";
	}

	@PostMapping("/document/send/list/filter")
	public String listFilter(Model model, WebRequest webRequest) {
		List<SendDocument> pageContainer = documentService.documentList(0, 100);
		model.addAttribute("documentList", pageContainer);
		model.addAttribute("selectedIdList", new SendDocumentStatusBachUdpateInfo());
		model.addAttribute("statusList", SendDocumentStatus.values());
		return "/document/send/list";
	}

	@PostMapping("/document/send/updatestatuses")
	public String listFilter(@ModelAttribute SendDocumentStatusBachUdpateInfo idList, WebRequest webRequest,
			Authentication authentication) {
		List<Long> ids = idList.getIdList();
		SendDocumentStatus status = idList.getStatus();
		documentService.updateStatuses(ids, status, authentication.getName());
		PageDataContainer container = WebRequestUtil.getPageDataContainerByWebRequest(webRequest);
		if (container != null) {
			int page = container.getPage();
			int size = container.getSize();
			if (container.getSize() >= container.getTotalElements()) {
				page = 1;
			}
			return "redirect:/document/send/list" + "?page=" + page + "&size=" + size + "&orderBy=" + container.getOrderCol()+ "_" + container.getOrderDir();
		} else {
			return "redirect:/document/send/list";
		}
	}

	@PostMapping("/document/send/updatestatus")
	public String updateStatus(SendDocument document, RedirectAttributes ra, Authentication authentication) {
		Long id = document.getId();
		SendDocumentStatus documentStatus = document.getDocumentStatus();
		int count = documentService.updateStatus(id, documentStatus, authentication.getName());
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
	public String upload(@RequestParam("file") MultipartFile file,
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
					SendDocument document = documentService.sendFile(tempFile.toPath(),
							"Uploaded manually " + file.getOriginalFilename(), validateImmediately);
					redirectAttributes.addFlashAttribute("message",
							"Successfully uploaded file as a document with status " + document.getDocumentStatus());
					return "redirect:/document/send/view/" + document.getId();
				} catch (DocumentProcessStepException se) {
					log.error("Failed document processing", se);
					redirectAttributes.addFlashAttribute("errorMessage",
							"Failed to process file " + tempFile + " with error " + se.getMessage());
					if (se.getDocumentId() != null) {
						return "redirect:/document/send/view/" + se.getDocumentId();
					}

				} catch (Exception e) {
					log.error("Failed to load file " + tempFile, e);
					redirectAttributes.addFlashAttribute("errorMessage",
							"Failed to load file " + tempFile + " with error " + e.getMessage());
				}
			}
		}

		return "redirect:/document/send/list";
	}

	private boolean isDownloadAllowed(SendDocumentBytes b) {
		if (downloadAllowAll) {
			return true;
		}
		return b.getType() == SendDocumentBytesType.RECEIPT;
	}

	@GetMapping("/document/send/download/{documentId}/{bytesId}")
	public ResponseEntity<Object> download(@PathVariable long documentId, @PathVariable long bytesId,
			RedirectAttributes ra) throws IOException {
		SendDocumentBytes sendDocumentBytes = documentService.findDocumentBytes(documentId, bytesId);
		if (sendDocumentBytes == null) {
			ra.addFlashAttribute("errorMessage", "Data not found");
			return RedirectUtil.redirectEntity(servletContextPath, "/document/send/view/" + documentId);
		}
		if (!isDownloadAllowed(sendDocumentBytes)) {
			ra.addFlashAttribute("errorMessage", "Only RECEIPT bytes are allowed for download, but "
					+ sendDocumentBytes.getType() + " is requested");
			return RedirectUtil.redirectEntity(servletContextPath, "/document/send/view/" + documentId);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.documentService.getDocumentBytesContents(sendDocumentBytes, out);
		byte[] data = out.toByteArray();
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"data_" + documentId + "_" + bytesId + ".xml\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
	}
}
