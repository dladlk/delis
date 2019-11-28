package dk.erst.delis.task.document.process;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.SchemaValidator;
import dk.erst.delis.task.document.process.validate.SchematronValidator;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.process.validate.result.ISchematronResultCollector;
import dk.erst.delis.task.document.process.validate.result.SchematronResultCollectorFactory;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentValidationTransformationService {

	private final RuleService ruleService;
	private final DocumentParseService documentParseService;

	@Autowired
	public DocumentValidationTransformationService(RuleService ruleService, DocumentParseService documentParseService) {
		this.ruleService = ruleService;
		this.documentParseService = documentParseService;
	}
	public DocumentProcessLog process(Document document, Path xmlLoadedPath, OrganisationReceivingFormatRule receivingFormatRule, TransformationResultListener transformationResultListener) {
		return this.process(document, xmlLoadedPath, receivingFormatRule, transformationResultListener, true, false);
	}
	
	public DocumentProcessLog process(Document document, Path xmlLoadedPath, OrganisationReceivingFormatRule receivingFormatRule, TransformationResultListener transformationResultListener, boolean stopOnFirstError, boolean skipPEPPOL) {
		DocumentFormat ingoingDocumentFormat = document.getIngoingDocumentFormat();
		DocumentProcessLog plog = new DocumentProcessLog();

		plog.setResultPath(xmlLoadedPath);

		try {
			processAllFormats(plog, xmlLoadedPath, ingoingDocumentFormat, receivingFormatRule, transformationResultListener, stopOnFirstError, skipPEPPOL);
		} catch (Exception e) {
			log.error("Failed to process all formats on document " + document + " by path " + xmlLoadedPath, e);
		}

		return plog;
	}

	private void processAllFormats(DocumentProcessLog plog, Path xmlPath, DocumentFormat documentFormat, OrganisationReceivingFormatRule receivingFormatRule, TransformationResultListener transformationListener, boolean stopOnFirstError, boolean skipPEPPOL) {
		List<RuleDocumentValidation> ruleByFormat = ruleService.getValidationRuleListByFormat(documentFormat);
		for (RuleDocumentValidation ruleDocumentValidation : ruleByFormat) {
			if (skipPEPPOL && ruleDocumentValidation.getValidationType() == RuleDocumentValidationType.SCHEMATRON) {
				String schematronPath = ruleDocumentValidation.getRootPath();
				if (schematronPath != null && schematronPath.contains("PEPPOL")) {
					continue;
				}
			}
			try (InputStream xmlStream = new BufferedInputStream(new FileInputStream(xmlPath.toFile()), (int)xmlPath.toFile().length())) {
				xmlStream.mark(Integer.MAX_VALUE);
				DocumentProcessStep step = validateByRule(xmlStream, ruleDocumentValidation);
				plog.addStep(step);
				if (!step.isSuccess() && stopOnFirstError) {
					plog.setLastDocumentFormat(documentFormat);
					return;
				}
			} catch (IOException e) {
				log.error("Failed to validate " + xmlPath + " by rule " + ruleDocumentValidation, e);
				DocumentProcessStep step = new DocumentProcessStep(ruleDocumentValidation);
				ErrorRecord err = new ErrorRecord(ruleDocumentValidation.buildErrorCode(), "", e.getMessage(), "error", "IOException");
				step.addError(err);
				step.setMessage(e.getMessage());
				plog.addStep(step);
			}
		}

		if (receivingFormatRule.isLast(documentFormat)) {
			plog.setLastDocumentFormat(documentFormat);
			return;
		}

		DocumentFormatFamily formatFamily = documentFormat.getDocumentFormatFamily();
		RuleDocumentTransformation transformationRule = ruleService.getTransformation(formatFamily);
		Path xmlOutPath = null;
		if (transformationRule != null) {
			String prefix = "transformation_" + formatFamily + "_to_" + transformationRule.getDocumentFormatFamilyTo();
			xmlOutPath = createTempFile(plog, xmlOutPath, prefix);
			if (xmlOutPath == null) {
				return;
			}
			plog.setResultPath(xmlOutPath);
			DocumentProcessStep step = transformByRule(xmlPath, xmlOutPath, transformationRule);
			plog.addStep(step);
			if (!step.isSuccess()) {
				return;
			}
		}

		DocumentFormat resultFormat = identifyResultFormat(plog, xmlOutPath);
		if (transformationListener != null) {
			/*
			 * Do not save INTERM version if it is last format already
			 */
			if (!receivingFormatRule.isLast(resultFormat)) {
				transformationListener.notify(plog, resultFormat, xmlOutPath.toFile());
			}
		}

		processAllFormats(plog, xmlOutPath, resultFormat, receivingFormatRule, transformationListener, stopOnFirstError, skipPEPPOL);
	}

	protected DocumentFormat identifyResultFormat(DocumentProcessLog plog, Path xmlPath) {
		DocumentProcessStep step = new DocumentProcessStep("Define format of " + xmlPath, DocumentProcessStepType.RESOLVE_TYPE);
		DocumentFormat format = DocumentFormat.UNSUPPORTED;
		try {
			format = documentParseService.defineDocumentFormat(new FileInputStream(xmlPath.toFile()));
			step.setResult(format);
			if (!format.isUnsupported()) {
				step.setSuccess(true);
			}
		} catch (Exception e) {
			log.error("Failed to define document format of " + xmlPath, e);
		} finally {
			step.done();
			plog.addStep(step);
		}
		return format;
	}

	protected DocumentProcessStep transformByRule(Path xmlPath, Path xmlOutPath, RuleDocumentTransformation transformationRule) {
		return transformByRule(xmlPath, xmlOutPath, transformationRule, ruleService);
	}
	
	public static DocumentProcessStep transformByRule(Path xmlPath, Path xmlOutPath, RuleDocumentTransformation transformationRule, RuleService ruleService) {
		DocumentProcessStep step = new DocumentProcessStep(transformationRule);

		Path xslFilePath = ruleService.filePath(transformationRule);
		try (FileInputStream xslStream = new FileInputStream(xslFilePath.toFile());
			FileInputStream xmlStream = new FileInputStream(xmlPath.toFile());
			FileOutputStream resultStream = new FileOutputStream(xmlOutPath.toFile())) {
			XSLTUtil.apply(xslStream, xslFilePath, new CloseShieldInputStream(xmlStream), resultStream);
			step.setSuccess(true);
			step.setResult(xmlOutPath);
		} catch (Exception e) {
			log.error("Failed to transform " + xmlPath + " with " + transformationRule, e);
			step.setMessage(e.getMessage());
		} finally {
			step.done();
		}

		return step;
	}

	public DocumentProcessStep validateByRule(InputStream xmlStream, RuleDocumentValidation ruleDocumentValidation) {
		DocumentProcessStep step = new DocumentProcessStep(ruleDocumentValidation);

		try {
			switch (ruleDocumentValidation.getValidationType()) {
				case XSD:
					SchemaValidator xsdValidator = new SchemaValidator();
					try {
						xmlStream.reset();
						List<ErrorRecord> errorList = xsdValidator.validate(xmlStream, ruleService.filePath(ruleDocumentValidation), ruleDocumentValidation);
						step.setSuccess(errorList.isEmpty());
						step.setErrorRecords(errorList);
					} catch (Exception e) {
						log.error("Failed validation by rule " + ruleDocumentValidation, e);
						step.setMessage(e.getMessage());
					}

					break;
				case SCHEMATRON:
					SchematronValidator schValidator = new SchematronValidator();
					Path xslFilePath = ruleService.filePath(ruleDocumentValidation);
					try (InputStream schematronStream = new FileInputStream(xslFilePath.toFile())) {
						ISchematronResultCollector collector = SchematronResultCollectorFactory.getCollector(ruleDocumentValidation.getDocumentFormat());
						xmlStream.reset();
						List<ErrorRecord> errorList = schValidator.validate(xmlStream, schematronStream, collector, xslFilePath);
						boolean success = isEmptyOrOnlyWarnings(errorList);
						step.setSuccess(success);
						step.setErrorRecords(errorList);
					} catch (Exception e) {
						log.error("Failed validation by rule " + ruleDocumentValidation, e);
						step.setMessage(e.getMessage());
					}

					break;
			}
		} finally {
			step.done();
		}

		return step;
	}

	private boolean isEmptyOrOnlyWarnings(List<ErrorRecord> errorList) {
		boolean success = true;
		if (errorList != null) {
			for (ErrorRecord errorRecord : errorList) {
				if (!errorRecord.isWarning()) {
					success = false;
					break;
				}
			}
		}
		return success;
	}

	public Path createTempFile(DocumentProcessLog plog, Path xmlOutPath, String prefix) {
		DocumentProcessStep step = new DocumentProcessStep("Create temp file with prefix " + prefix, DocumentProcessStepType.COPY);
		try {
			xmlOutPath = Files.createTempFile(prefix+"_", ".xml");
			step.setSuccess(true);
			step.setResult(xmlOutPath);
		} catch (Exception e) {
			log.error("Failed to create temp file", e);
			return null;
		} finally {
			plog.addStep(step);
			step.done();
		}
		return xmlOutPath;
	}

}
