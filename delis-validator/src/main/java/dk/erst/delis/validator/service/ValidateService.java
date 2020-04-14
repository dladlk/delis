package dk.erst.delis.validator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.rule.DefaultRuleBuilder;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.config.ConfigValueType;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.task.document.parse.DocumentInfoService;
import dk.erst.delis.task.document.parse.DocumentInfoService.DocumentInfoData;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.process.DocumentValidationTransformationService;
import dk.erst.delis.task.document.process.RuleService;
import dk.erst.delis.task.document.process.TransformationResultListener;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.validator.DelisValidatorConfig;
import dk.erst.delis.validator.service.dao.DummyConfigRepository;
import dk.erst.delis.validator.service.dao.DummyTransformationRepository;
import dk.erst.delis.validator.service.dao.DummyValidationRepository;
import dk.erst.delis.validator.service.input.IValidateFileInput;
import dk.erst.delis.web.transformationrule.TransformationRuleService;
import dk.erst.delis.web.validationrule.ValidationRuleService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidateService {

	private DelisValidatorConfig config;

	private DocumentInfoService documentInfoService;
	private DocumentParseService documentParseService;
	private DocumentValidationTransformationService service;
	
	private List<RuleDocumentValidation> validationRuleList;

	@Autowired
	public ValidateService(DelisValidatorConfig config) {
		this.config = config;
		this.documentParseService = new DocumentParseService();
		this.documentInfoService = new DocumentInfoService(documentParseService);
		this.service = buildService();
	}

	public ValidateResult validateFile(IValidateFileInput file, boolean skipPEPPOL) {
		ValidateResult result = new ValidateResult();

		File tempFile = null;
		try {
			result.setFileName(file.getOriginalFilename());

			tempFile = File.createTempFile("manual_upload_" + file.getName() + "_", ".xml");
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				StreamUtils.copy(file.getInputStream(), fos);
			}
			log.info("Saved file " + file.getOriginalFilename() + " as test file " + tempFile);
		} catch (IOException e) {
			log.error("Failed to save uploaded file to temp for " + file.getName(), e);
		}

		DocumentInfoData infoData = null;

		if (tempFile != null) {
			try {
				result.setFileSize(tempFile.length());

				DocumentProcessStep step = DocumentProcessStep.buildDefineFormatStep();

				infoData = documentInfoService.documentInfoData(tempFile.toPath(), tempFile);
				DocumentFormat documentFormat = documentInfoService.defineDocumentFormat(infoData.getDocumentInfo());

				result.setDocumentFormat(documentFormat);
				result.setDocumentFormatDetected(true);

				tempFile = infoData.getFile();

				if (documentFormat.isUnsupported()) {
					DocumentProcessLog plog = new DocumentProcessLog();
					plog.setResultPath(tempFile.toPath());

					step.fillDefineFormatError(infoData.getDocumentInfo());

					plog.addStep(step);

					result.setProcessLog(plog);

					return result;
				}

				Document document = new Document();
				document.setIngoingDocumentFormat(result.getDocumentFormat());

				OrganisationReceivingFormatRule receivingFormatRule = OrganisationReceivingFormatRule.OIOUBL;
				Path xmlLoadedPath = tempFile.toPath();
				TransformationResultListener transformationResultListener = new TransformationResultListener(null, null) {
					@Override
					public void notify(DocumentProcessLog plog, DocumentFormat resultFormat, File file) {
						/*
						 * Avoid notification - as we do not want to save documents into byte storage
						 */
					}
				};
				boolean stopOnFirstError = true;
				DocumentProcessLog plog = service.process(document, xmlLoadedPath, receivingFormatRule, transformationResultListener, stopOnFirstError, skipPEPPOL);
				result.setProcessLog(plog);

			} finally {
				if (infoData != null) {
					deleteFile(infoData.getFile());
					deleteFile(infoData.getFileSbd());
				} else {
					deleteFile(tempFile);
				}
			}
		}

		return result;
	}

	private DocumentValidationTransformationService buildService() {
		DocumentValidationTransformationService service = null;

		final List<ConfigValue> configList = new ArrayList<ConfigValue>();

		ConfigValue cv = new ConfigValue();
		cv.setConfigValueType(ConfigValueType.STORAGE_VALIDATION_ROOT);
		cv.setValue(this.config.getStorageValidationRoot());
		configList.add(cv);

		ConfigBean configBean = new ConfigBean(new DummyConfigRepository(configList));

		validationRuleList = DefaultRuleBuilder.buildDefaultValidationRuleList();
		ValidationRuleService validationRuleService = new ValidationRuleService(new DummyValidationRepository(validationRuleList));
		TransformationRuleService transformationRuleService = new TransformationRuleService(new DummyTransformationRepository(Collections.emptyList()));
		RuleService ruleService = new RuleService(configBean, validationRuleService, transformationRuleService);
		service = new DocumentValidationTransformationService(ruleService, documentParseService);
		return service;
	}

	private void deleteFile(File f) {
		if (f != null) {
			if (!f.delete()) {
				f.deleteOnExit();
				log.info("Cannot delete file " + f + ", requested to delete on exit");
			} else {
				log.info("Successfully deleted file " + f);
			}
		}
	}

	public List<RuleDocumentValidation> getValidationRuleList() {
		return validationRuleList;
	}
}
