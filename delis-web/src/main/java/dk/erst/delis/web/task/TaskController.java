package dk.erst.delis.web.task;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

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
import dk.erst.delis.task.scheduler.TaskScheduler;
import dk.erst.delis.web.RedirectUtil;
import dk.erst.delis.web.document.ExportDocumentHistoryService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TaskController {

	@Autowired
	private ConfigBean configBean;

	@Autowired
	private TaskScheduler taskScheduler;
	
	@Autowired
	private ExportDocumentHistoryService exportDocumentHistoryService;

	@GetMapping("/task/index")
	public String index() {
		return "task/index";
	}

	@GetMapping("/task/identifierLoad")
	public String identifierLoad(Model model) {
		try {
			StatData sd = taskScheduler.identifierLoad();
			model.addAttribute("message", "Done load identifiers in " + sd.toDurationString() + ": " + sd);
		} catch (Throwable e) {
			model.addAttribute("errorMessage", e.getClass().getSimpleName() + ": " + e.getMessage());
			log.error(e.getMessage(), e);
		}
		return "task/index";
	}

	@GetMapping("/task/identifierPublish")
	public String identifierPublish(Model model) {
		try {
			StatData statData = taskScheduler.identifierPublish();
			model.addAttribute("message", "Done publish to SMP in " + statData.toDurationString() + ": " + statData);
			return "task/index";
		} catch (Throwable e) {
			model.addAttribute("errorMessage", e.getClass().getSimpleName() + ": " + e.getMessage());
			log.error(e.getMessage(), e);
		}
		return "task/index";
	}

	@GetMapping("/task/documentLoad")
	public String documentLoad(Model model) {
		Path inputFolderPath = configBean.getStorageInputPath().toAbsolutePath();

		File inputFolderFile = inputFolderPath.toFile();
		if (!inputFolderFile.exists() || !inputFolderFile.isDirectory()) {
			model.addAttribute("errorMessage", "Document input folder " + inputFolderPath + " does not exist or is not a directory");
			return "task/index";
		}

		try {
			StatData sd = taskScheduler.documentLoad();
			String loadStatStr = sd.toStatString();
			String message = "Done received document loading from " + inputFolderPath + " in " + sd.toDurationString() + ": " + loadStatStr;
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentLoadService.loadFromInput", e);
			model.addAttribute("errorMessage", "Failed to load documents from folder " + inputFolderPath + ": " + e.getMessage());
		}

		return "task/index";
	}

	@GetMapping("/task/documentValidate")
	public String documentValidate(Model model) {
		try {
			StatData sd = taskScheduler.documentValidate();
			String message = "Done processing of loaded files in " + sd.toDurationString() + ": " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentListProcessService.processLoaded", e);
			model.addAttribute("errorMessage", "Failed to process loaded documents: " + e.getMessage());
		}
		return "task/index";
	}

	@GetMapping("/task/documentDeliver")
	public String documentDeliver(Model model) {
		try {
			StatData sd = taskScheduler.documentDeliver();
			String message = "Done document delivery in " + sd.toDurationString() + ": " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentDeliveryService.processValidated", e);
			model.addAttribute("errorMessage", "Failed to deliver validated documents: " + e.getMessage());
		}
		return "task/index";
	}

	@GetMapping("/task/documentCheckDelivered")
	public String documentCheckDelivered(Model model) {
		try {
			StatData sd = taskScheduler.documentCheckDelivery();
			String message = "Done delivery check in " + sd.toDurationString() + ": " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke documentDeliveryCheckService.process", e);
			model.addAttribute("errorMessage", "Failed to check document delivery: " + e.getMessage());
		}
		return "task/index";
	}
	
	@GetMapping("/task/sendDocumentLoad")
	public String sendDocumentLoad(Model model) {
		try {
			StatData sd = taskScheduler.sendDocumentLoad();
			String message = "Done loading of send folder for new documents to send in " + sd.toDurationString() + ": " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke sendDocumentService.loadNewDocuments", e);
			model.addAttribute("errorMessage", "Failed to load documents to send: " + e.getMessage());
		}
		return "task/index";
	}

	@GetMapping("/task/sendDocumentValidate")
	public String sendDocumentValidate(Model model) {
		try {
			StatData sd = taskScheduler.sendDocumentValidate();
			String message = "Done processing of NEW sent documents in " + sd.toDurationString() + ": " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke sendDocumentService.validateNewDocuments", e);
			model.addAttribute("errorMessage", "Failed to validate sent documents: " + e.getMessage());
		}
		return "task/index";
	}

	@GetMapping("/task/sendFailedProcess")
	public String sendFailedProcess(Model model) {
		try {
			StatData sd = taskScheduler.sendDocumentFailedProcess();
			String message = "Done processing of SEND_FAILED sent documents in " + sd.toDurationString() + " with result: " + sd.toStatString();
			model.addAttribute("message", message);
		} catch (Exception e) {
			log.error("Failed to invoke sendDocumentFailedProcessService.processFailedDocuments", e);
			model.addAttribute("errorMessage", "Failed to process failed sent documents: " + e.getMessage());
		}
		return "task/index";
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
		return "task/index";
	}
}
