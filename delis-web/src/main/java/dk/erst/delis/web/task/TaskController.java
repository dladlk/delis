package dk.erst.delis.web.task;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.identifier.publish.IdentifierBatchPublishingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

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
	private IdentifierBatchPublishingService identifierBatchPublishingService;


	@GetMapping("/task/index")
	public String index() {
		return "/task/index";
	}

	@GetMapping("/task/identifierLoad")
	public String identifierLoad(Model model) {
		return unimplemented(model);
	}

	@GetMapping("/task/identifierPublish")
	public String identifierPublish(Model model) {
		try {
			List<Long> publishedIdentifierIds = identifierBatchPublishingService.publishPending();
			String message = String.format("%d identifiers published to SMP", publishedIdentifierIds.size());
			model.addAttribute("message", message);
			log.info(message);
		} catch (Throwable e) {
			model.addAttribute("errorMessage", e.getClass().getSimpleName()+": "+e.getMessage());
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
		return unimplemented(model);
	}

	@GetMapping("/task/unimplemented")
	private String unimplemented(Model model) {
		model.addAttribute("errorMessage", "Not implemented");
		return "/task/index";
	}
}
