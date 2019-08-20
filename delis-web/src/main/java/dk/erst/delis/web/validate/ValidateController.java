package dk.erst.delis.web.validate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.TransformationResultListener;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ValidateController {

	@Autowired
	private DocumentValidationTransformationService documentValidationTransformationService;

	@RequestMapping("/validate/index")
	public String index(Model model, WebRequest webRequest) {

		fillModel(model);

		return "/validate/validate";
	}

	private void fillModel(Model model) {
		model.addAttribute("formatList", DocumentFormat.values());
	}

	@PostMapping("/validate/upload")
	public String upload(

			@RequestParam("files") MultipartFile[] files,

			@RequestParam(name = "validateFormat", required = false) String validateFormatName,

			Model model,

			WebRequest webRequest,

			RedirectAttributes redirectAttributes) {

		DocumentFormat ingoingDocumentFormat = null;
		if (!StringUtils.isEmpty(validateFormatName)) {
			ingoingDocumentFormat = DocumentFormat.valueOf(validateFormatName);
		}

		if (ingoingDocumentFormat == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Please select document format. Autodetection of format will be added later.");
			return "redirect:/validate/index";
		}

		if (files == null || files.length == 0) {
			redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one XML file");
			return "redirect:/validate/index";
		}

		List<ValidateResult> resultList = new ArrayList<ValidateResult>();

		for (MultipartFile file : files) {

			ValidateResult result = new ValidateResult();

			File tempFile = null;
			try {
				result.setFileName(file.getOriginalFilename());
				tempFile = File.createTempFile("manual_upload_" + file.getName() + "_", ".xml");
				try (FileOutputStream fos = new FileOutputStream(tempFile)) {
					IOUtils.copy(file.getInputStream(), fos);
				}
			} catch (IOException e) {
				log.error("Failed to save uploaded file to temp for " + file.getName(), e);
			}

			result.setDocumentFormat(ingoingDocumentFormat);

			try {
				if (tempFile != null) {
					log.info("Created test file " + tempFile);
					result.setFileSize(tempFile.length());
					Document document = new Document();
					document.setIngoingDocumentFormat(result.getDocumentFormat());

					OrganisationReceivingFormatRule receivingFormatRule = OrganisationReceivingFormatRule.OIOUBL;
					Path xmlLoadedPath = tempFile.toPath();
					TransformationResultListener transformationResultListener = new TransformationResultListener(null, null) {
						@Override
						public void notify(DocumentProcessLog plog, DocumentFormat resultFormat, File file) {
							super.notify(plog, resultFormat, file);
						}
					};
					DocumentProcessLog plog = documentValidationTransformationService.process(document, xmlLoadedPath, receivingFormatRule, transformationResultListener);
					result.setProcessLog(plog);

					resultList.add(result);

				}
			} finally {
				tempFile.delete();
			}
		}

		model.addAttribute("resultList", resultList);
		model.addAttribute("message", "Done validation of " + files.length + " uploaded XML files");
		fillModel(model);

		return "/validate/validate";
	}

	@Getter
	@Setter
	private static class ValidateResult {

		private String fileName;
		private long fileSize;
		private DocumentFormat documentFormat;
		private DocumentProcessLog processLog;

	}
}
