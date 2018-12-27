package dk.erst.delis.web.task;

import java.io.File;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentListProcessService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TaskController {

	@Autowired
	private DocumentLoadService documentLoadService;

	@Autowired
	private DocumentListProcessService documentListProcessService;

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
		return unimplemented(model);
	}

	@GetMapping("/task/documentLoad")
	public String documentLoad(Model model) {
		Path inputFolderPath = documentLoadService.getInputFolderPath().toAbsolutePath();

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
			StatData sd = documentListProcessService.processLoaded();
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
