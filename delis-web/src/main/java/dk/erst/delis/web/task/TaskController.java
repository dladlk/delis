package dk.erst.delis.web.task;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.deliver.DocumentDeliverService;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.document.send.forward.SendDocumentFailedProcessService;
import dk.erst.delis.task.identifier.load.IdentifierBatchLoadService;
import dk.erst.delis.task.identifier.load.OrganizationIdentifierLoadReport;
import dk.erst.delis.task.identifier.publish.IdentifierBatchPublishingService;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.document.ExportDocumentHistoryService;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TaskController {

	@Autowired
	private ConfigBean configBean;

	@Autowired
	private DocumentLoadService documentLoadService;

	@Autowired
	private DocumentProcessService documentProcessService;

	@Autowired
	private DocumentDeliverService documentDeliverService;

	@Autowired
	private IdentifierBatchPublishingService identifierBatchPublishingService;

	@Autowired
	private IdentifierBatchLoadService identifierBatchLoadService;

	@Autowired
	private SendDocumentService sendDocumentService;

	@Autowired
	private SendDocumentFailedProcessService sendDocumentFailedProcessService;

	@Autowired
	private ExportDocumentHistoryService exportDocumentHistoryService;

	@GetMapping("/task/index")
	public String index() {
		return "/task/index";
	}

	@GetMapping("/task/identifierLoad")
	public String identifierLoad(Model model) {
		try {
			List<OrganizationIdentifierLoadReport> loadReports = identifierBatchLoadService.performLoad();
			String message = identifierBatchLoadService.createReportMessage(loadReports);
			model.addAttribute("message", message);
			log.info(message);
		} catch (Throwable e) {
			model.addAttribute("errorMessage", e.getClass().getSimpleName() + ": " + e.getMessage());
			log.error(e.getMessage(), e);
		}
		return "/task/index";
	}

	@GetMapping("/task/identifierPublish")
	public String identifierPublish(Model model) {
		try {
			List<Long> publishedIdentifierIds = identifierBatchPublishingService.publishPending();
			String message = String.format("%d identifiers published to SMP", publishedIdentifierIds.size());
			model.addAttribute("message", message);
			log.info(message);
		} catch (Throwable e) {
			model.addAttribute("errorMessage", e.getClass().getSimpleName() + ": " + e.getMessage());
			log.error(e.getMessage(), e);
		}
		return "/task/index";
	}

	@GetMapping("/task/documentLoad")
	public String documentLoad(Model model) {
		Path inputFolderPath = configBean.getStorageInputPath().toAbsolutePath();

		File inputFolderFile = inputFolderPath.toFile();
		if (!inputFolderFile.exists() || !inputFolderFile.isDirectory()) {
			model.addAttribute("errorMessage", "Document input folder " + inputFolderPath + " does not exist or is not a directory");
			return "/task/index";
		}

		try {
			StatData sd = documentLoadService.loadFromInput(inputFolderPath);
			String loadStatStr = sd.toStatString();
			String message = "Done loading from folder " + inputFolderPath + " in " + sd.toDurationString() + " with next statisics of document status: " + loadStatStr;
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentLoadService.loadFromInput", e);
			model.addAttribute("errorMessage", "Failed to load documents from folder " + inputFolderPath + ": " + e.getMessage());
		}

		return "/task/index";
	}

	@GetMapping("/task/documentValidate")
	public String documentValidate(Model model) {
		try {
			StatData sd = documentProcessService.processLoaded();
			String message = "Done processing of loaded files in " + sd.toDurationString() + " with result: " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentListProcessService.processLoaded", e);
			model.addAttribute("errorMessage", "Failed to process loaded documents: " + e.getMessage());
		}
		return "/task/index";
	}

	@GetMapping("/task/documentDeliver")
	public String documentDeliver(Model model) {
		try {
			StatData sd = documentDeliverService.processValidated();
			String message = "Done processing of validated files in " + sd.toDurationString() + " with result: " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentDeliveryService.processValidated", e);
			model.addAttribute("errorMessage", "Failed to deliver validated documents: " + e.getMessage());
		}
		return "/task/index";
	}

	@GetMapping("/task/documentCheckDelivered")
	public String documentCheckDelivered(Model model) {
		return unimplemented(model);
	}
	
	@GetMapping("/task/sendDocumentValidate")
	public String sendDocumentValidate(Model model) {
		try {
			StatData sd = sendDocumentService.validateNewDocuments();
			String message = "Done processing of NEW sent documents in " + sd.toDurationString() + " with result: " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke sendDocumentService.validateNewDocuments", e);
			model.addAttribute("errorMessage", "Failed to validate sent documents: " + e.getMessage());
		}
		return "/task/index";
	}

	@GetMapping("/task/sendFailedProcess")
	public String sendFailedProcess(Model model) {
		try {
			StatData sd = sendDocumentFailedProcessService.processFailedDocuments();
			String message = "Done processing of SEND_FAILED sent documents in " + sd.toDurationString() + " with result: " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke sendDocumentFailedProcessService.processFailedDocuments", e);
			model.addAttribute("errorMessage", "Failed to process failed sent documents: " + e.getMessage());
		}
		return "/task/index";
	}

	@GetMapping("/task/exportHistory")
	public ResponseEntity<Object> exportHistory(Model model, RedirectAttributes ra) {
		try {
			File file = File.createTempFile("export_history", "txt");
			try {
				exportDocumentHistoryService.generateExportFile(file);

				BodyBuilder resp = ResponseEntity.ok();
				resp.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.csv\"");
				resp.contentType(MediaType.parseMediaType("application/octet-stream"));
				return resp.body(new InputStreamResource(new FileInputStream(file)));
			} finally {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		} catch (Exception e) {
			log.error("Failed to export history", e);
			ra.addAttribute("errorMessage", "Failed to export history: " + e.getMessage());
		}
		return RedirectUtil.redirectEntity("/task/index");
	}

	@GetMapping("/task/unimplemented")
	private String unimplemented(Model model) {
		model.addAttribute("errorMessage", "Not implemented");
		return "/task/index";
	}
}
