package dk.erst.delis.web.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

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
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.dao.ICriteriaCustomizer;
import dk.erst.delis.web.datatables.data.PageData;
import dk.erst.delis.web.datatables.service.EasyDatatablesListService;
import dk.erst.delis.web.datatables.service.EasyDatatablesListServiceImpl;
import dk.erst.delis.web.document.ir.ApplicationResponseFormController;
import dk.erst.delis.web.error.ErrorDictionaryService;
import dk.erst.delis.web.list.AbstractEasyListController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DocumentController extends AbstractEasyListController<Document> implements ICriteriaCustomizer<Document> {

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

	@Value("${delis.download.allow.all:#{false}}")
	private boolean downloadAllowAll;

	@Autowired
	private OrganisationService organisationService; 

	@Autowired
	private ErrorDictionaryService errorDictionaryService;

	/*
	 * START EasyDatatables block
	 */
	@Autowired
	private DocumentDataTableRepository documentDataTableRepository;
	@Autowired
	private EasyDatatablesListServiceImpl<Document> documentEasyDatatablesListService;

	@Override
	protected String getListCode() {
		return "document";
	}

	@Override
	protected DataTablesRepository<Document, Long> getDataTableRepository() {
		return this.documentDataTableRepository;
	}

	@Override
	protected EasyDatatablesListService<Document> getEasyDatatablesListService() {
		return documentEasyDatatablesListService;
	}
	
	@Override
	protected ICriteriaCustomizer<Document> getCriteriaCustomizer() {
		return this;
	}	

	@RequestMapping("/document/list")
	public String list(Model model, WebRequest webRequest) {
		model.addAttribute("selectedIdList", new DocumentStatusBachUdpateInfo());
		model.addAttribute("statusList", DocumentStatus.values());
		model.addAttribute("documentFormatList", DocumentFormat.values());
		model.addAttribute("documentTypeList", DocumentType.values());
		model.addAttribute("organisationList", organisationService.getOrganisations());

		String res = super.list(model, webRequest);

		PageData pageData = this.getPageData();
		if (pageData != null) {
			Long errorDictionaryId = ((DocumentPageData)pageData).getErrorDictionaryId();
			if (errorDictionaryId != null) {
				model.addAttribute("errorDictionary", errorDictionaryService.getErrorDictionary(errorDictionaryId));
			}
		}

		return res;
	}

	@Override
	public PageData buildInitialPageData() {
		return new DocumentPageData();
	}

	@Override
	protected PageData updatePageData(WebRequest webRequest) {
		PageData pageData = super.updatePageData(webRequest);

		String errorDictionaryIdStr = webRequest.getParameter("errorDictionaryId");
		if (errorDictionaryIdStr != null) {
			long errorDictionaryId = Long.parseLong(errorDictionaryIdStr);
			((DocumentPageData) pageData).setErrorDictionaryId(errorDictionaryId);
		}

		return pageData;
	}
	/*
	 * END EasyDatatables block
	 */

	@RequestMapping("/document/listOld")
	public String listOld(Model model) {
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
			return RedirectUtil.redirectEntity("/document/view/" + documentId);
		}
		if (!isDownloadAllowed(documentBytes)) {
			ra.addFlashAttribute("errorMessage", "Only RECEIPT bytes are allowed for download, but " + documentBytes.getType() + " is requested");
			return RedirectUtil.redirectEntity("/document/view/" + documentId);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.documentService.getDocumentBytesContents(documentBytes, out);
		byte[] data = out.toByteArray();
		BodyBuilder resp = ResponseEntity.ok();
		resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data_" + documentId + "_" + bytesId + ".xml\"");
		resp.contentType(MediaType.parseMediaType("application/octet-stream"));
		return resp.body(new InputStreamResource(new ByteArrayInputStream(data)));
	}

	public static class DocumentPageData extends PageData {
		private static final long serialVersionUID = -4788973622822908206L;

		@Getter
		@Setter
		private Long errorDictionaryId;
		
		public void clear() {
			super.clear();
			this.errorDictionaryId = null;
			this.setOrder("createTime_desc");
		}
	}

	@Override
	public Predicate customPredicates(PageData pageData, CriteriaBuilder builder, CriteriaQuery<?> query, Root<Document> root) {
		DocumentPageData dpd = (DocumentPageData)pageData;
		if (dpd.getErrorDictionaryId() != null) {
			Subquery<JournalDocumentError> subquery = query.subquery(JournalDocumentError.class);
			Root<JournalDocumentError> subqueryRoot = subquery.from(JournalDocumentError.class);
			subquery.select(subqueryRoot);
			subquery.where(builder.and(builder.equal(root, subqueryRoot.get("journalDocument").get("document")),
					subqueryRoot.get("errorDictionary").get("id").in(dpd.getErrorDictionaryId()))
			);
			return builder.exists(subquery);
		}
		return null;
	}
}
