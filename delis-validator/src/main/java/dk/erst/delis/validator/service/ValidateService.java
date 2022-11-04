package dk.erst.delis.validator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

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
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
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

	private PersistService persistService;
	private ValidateStatBean validateStatBean;

	@Autowired
	public ValidateService(DelisValidatorConfig config, PersistService persistService, ValidateStatBean validateStatBean) {
		this.config = config;
		this.persistService = persistService;
		this.validateStatBean = validateStatBean;

		this.documentParseService = new DocumentParseService();
		this.documentInfoService = new DocumentInfoService(documentParseService);
		this.service = buildService();
	}

	public ValidateResult validateFile(IValidateFileInput file, boolean skipPEPPOL) {
		return validateFile(file, skipPEPPOL, null);
	}

	public ValidateResult validateFile(IValidateFileInput file, boolean skipPEPPOL, Boolean compressedObj) {
		ValidateResult result = new ValidateResult();

		boolean compressed = compressedObj != null && compressedObj.booleanValue();

		File tempFile = null;
		try {
			tempFile = File.createTempFile("manual_upload_" + file.getName() + "_", ".xml");

			log.info("Saving file " + file.getOriginalFilename() + " as " + (compressed ? "compressed" : "") + " file " + tempFile);
			result.setFileName(file.getOriginalFilename());

			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				InputStream inputStream = file.getInputStream();
				if (compressed) {
					inputStream = new GZIPInputStream(inputStream);
				}
				StreamUtils.copy(inputStream, fos);
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
				result.setRestResult(generateRestResult(result));

				this.validateStatBean.increment(result);
				this.persistService.persist(tempFile, infoData, result);
			}
		}

		return result;
	}

	private ValidateRestResult generateRestResult(ValidateResult result) {
		ValidateRestResult restResult = new ValidateRestResult();
		DocumentProcessLog processLog = result.getProcessLog();
		List<DocumentProcessStep> stepList = processLog.getStepList();

		if (result.getProcessLog().isSuccess()) {
			restResult.httpStatusCode = config.getResponseResponseValidCode();
			restResult.status = ValidateResultStatus.OK;

			StringBuilder sb = new StringBuilder();

			sb.append("Status: ");
			sb.append(restResult.status);

			sb.append(". Details: Validated as ");
			sb.append(result.getDocumentFormat());

			for (DocumentProcessStep step : stepList) {
				sb.append(";\n ");
				sb.append(step.getDescription());
				sb.append(" -  ");
				sb.append(step.isSuccess() ? "OK" : "ERROR");
			}
			restResult.body = sb.toString();
		} else {
			restResult.httpStatusCode = config.getResponseResponseInvalidCode();

			DocumentProcessStep lastStep = stepList.get(stepList.size() - 1);
			boolean xsdValidation = false;
			if (lastStep.getStepType().isValidation() && lastStep.getStepType().isXsd()) {
				xsdValidation = true;
			}

			/*
			 * Unsupported format can mean also that XML is not valid - e.g. just TEXT is posted, so let's use it as a sign of non-xml data.
			 * 
			 * Of cause, it can also mean that we received unsupported format too...
			 */
			if (result.getDocumentFormat() == DocumentFormat.UNSUPPORTED) {
				restResult.status = ValidateResultStatus.INVALID_XML;
			} else {
				restResult.status = xsdValidation ? ValidateResultStatus.INVALID_XSD : ValidateResultStatus.INVALID_SCH;
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Status: ");
			sb.append(restResult.status);

			sb.append(". Details: Validated as ");
			sb.append(result.getDocumentFormat());

			String lastStepDescription = lastStep.getDescription();
			if (lastStepDescription != null) {
				String annoyingPrefix = "Validate with ";
				if (lastStepDescription.startsWith(annoyingPrefix)) {
					lastStepDescription = lastStepDescription.substring(annoyingPrefix.length());
				}
			}
			sb.append("; Invalid by ");
			sb.append(lastStepDescription);
			if (lastStep.getMessage() != null) {
				sb.append(": ");
				sb.append(lastStep.getMessage());
			}
			List<ErrorRecord> errorRecords = lastStep.getErrorRecords();
			if (errorRecords != null && !errorRecords.isEmpty()) {
				for (ErrorRecord errorRecord : errorRecords) {
					if (!errorRecord.isWarning()) {
						sb.append("\n\"");
						sb.append(errorRecord.getMessage());
						sb.append("\"\n");
						if (errorRecord.getDetailedLocation() != null) {
							sb.append(" at ");
							sb.append(errorRecord.getDetailedLocation());
						}
						break;
					}
				}
			}
			restResult.body = sb.toString();
		}
		return restResult;
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

	public List<RuleDocumentValidation> getValidationRuleList() {
		return validationRuleList;
	}
}
