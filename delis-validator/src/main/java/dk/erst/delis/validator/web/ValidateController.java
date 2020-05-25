package dk.erst.delis.validator.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dk.erst.delis.validator.service.ValidateResult;
import dk.erst.delis.validator.service.ValidateService;
import dk.erst.delis.validator.service.input.MultipartFileInput;

@Controller
public class ValidateController {

	@Autowired
	private ValidateService validateService;

	@GetMapping
	public String validate() {
		return "validate";
	}

	@PostMapping("/validate")
	public String upload(

			@RequestParam("file") MultipartFile[] files,

			@RequestParam(name = "skipPEPPOL", required = false) boolean skipPEPPOL,

			Model model

	) {

		if (files == null || files.length == 0) {
			return "Please select at least one XML file";
		}

		List<ValidateResult> resultList = new ArrayList<ValidateResult>();

		List<String> fileNames = new ArrayList<String>();

		for (MultipartFile file : files) {
			MultipartFileInput input = new MultipartFileInput(file);
			resultList.add(validateService.validateFile(input, skipPEPPOL));

			fileNames.add(file.getOriginalFilename());
		}

		model.addAttribute("resultList", resultList);
		model.addAttribute("message", "Done validation of " + files.length + " uploaded XML files");

		String customTitle = "";
		if (fileNames.size() > 1) {
			customTitle += fileNames.size() + " files: ";
		}
		customTitle += fileNames.stream().collect(Collectors.joining(", "));

		model.addAttribute("customTitle", customTitle);
		return "validate";
	}
}
