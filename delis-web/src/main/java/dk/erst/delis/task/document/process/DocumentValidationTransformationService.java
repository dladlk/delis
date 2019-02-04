package dk.erst.delis.task.document.process;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.SchemaValidator;
import dk.erst.delis.task.document.process.validate.SchematronValidator;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.process.validate.result.ISchematronResultCollector;
import dk.erst.delis.task.document.process.validate.result.SchematronResultCollectorFactory;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXParseException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class DocumentValidationTransformationService {

	private final RuleService ruleService;
	private final DocumentBytesStorageService documentBytesStorageService;
	private final DocumentParseService documentParseService;

	@Autowired
	public DocumentValidationTransformationService(RuleService ruleService, DocumentParseService documentParseService, DocumentBytesStorageService documentBytesStorageService) {
		this.ruleService = ruleService;
		this.documentParseService = documentParseService;
		this.documentBytesStorageService = documentBytesStorageService;
	}

	public DocumentProcessLog process(Document document, Path xmlPath) {
		DocumentFormat ingoingDocumentFormat = document.getIngoingDocumentFormat();
		DocumentProcessLog plog = new DocumentProcessLog();
		plog.setResultPath(xmlPath);
		try {
			processAllFormats(plog, xmlPath, ingoingDocumentFormat);
		} catch (Exception e) {
			log.error("Failed to process all formats on document " + document + " by path " + xmlPath, e);
		}

		return plog;
	}

	private void processAllFormats(DocumentProcessLog plog, Path xmlPath, DocumentFormat documentFormat) {
		List<RuleDocumentValidation> ruleByFormat = ruleService.getValidationRuleListByFormat(documentFormat);
		for (RuleDocumentValidation ruleDocumentValidation : ruleByFormat) {
			DocumentProcessStep step = validateByRule(xmlPath, ruleDocumentValidation);
			plog.addStep(step);
			if (!step.isSuccess()) {
				return;
			}
		}

		if (documentFormat.getDocumentFormatFamily().isLast()) {
			return;
		}

		DocumentFormatFamily formatFamily = documentFormat.getDocumentFormatFamily();
		RuleDocumentTransformation transformationRule = ruleService.getTransformation(formatFamily);
		Path xmlOutPath = null;
		if (transformationRule != null) {
			String prefix = "transformation_" + formatFamily + "_to_" + transformationRule.getDocumentFormatFamilyTo() + "_";
			xmlOutPath = documentBytesStorageService.createTempFile(plog, xmlOutPath, prefix);
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

		processAllFormats(plog, xmlOutPath, resultFormat);
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
		DocumentProcessStep step = new DocumentProcessStep(transformationRule);

		Path xslFilePath = ruleService.filePath(transformationRule);
		try (FileInputStream xslStream = new FileInputStream(xslFilePath.toFile());
				FileInputStream xmlStream = new FileInputStream(xmlPath.toFile());
				FileOutputStream resultStream = new FileOutputStream(xmlOutPath.toFile())) {
			XSLTUtil.apply(xslStream, xslFilePath, xmlStream, resultStream);
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

	protected DocumentProcessStep validateByRule(Path xmlPath, RuleDocumentValidation ruleDocumentValidation) {
		DocumentProcessStep step = new DocumentProcessStep(ruleDocumentValidation);

		try {
			switch (ruleDocumentValidation.getValidationType()) {
			case XSD:
				SchemaValidator xsdValidator = new SchemaValidator();
				try (InputStream xmlStream = new FileInputStream(xmlPath.toFile())) {
					xsdValidator.validate(xmlStream, ruleService.filePath(ruleDocumentValidation));
					step.setSuccess(true);
				} catch (SAXParseException se) {
					String location = "line " + se.getLineNumber() + ", column " + se.getColumnNumber();
					log.error("Failed validation of file " + xmlPath + ", location: " + location + " by rule " + ruleDocumentValidation, se);
					step.setMessage("At " + location + ": " + se.getMessage());
				} catch (Exception e) {
					log.error("Failed validation of file " + xmlPath + " by rule " + ruleDocumentValidation, e);
					step.setMessage(e.getMessage());
				}

				break;
			case SCHEMATRON:
				SchematronValidator schValidator = new SchematronValidator();
				try (InputStream xmlStream = new FileInputStream(xmlPath.toFile()); InputStream schematronStream = new FileInputStream(ruleService.filePath(ruleDocumentValidation).toFile())) {
					ISchematronResultCollector collector = SchematronResultCollectorFactory.getCollector(ruleDocumentValidation.getDocumentFormat());
					List<ErrorRecord> errorList = schValidator.validate(xmlStream, schematronStream, collector);
					step.setSuccess(errorList.isEmpty());
					step.setErrorRecords(errorList);
					if (!errorList.isEmpty()) {
						System.out.println("errorList = " + errorList);
					}

//					if (!errorList.isEmpty()) {
//						StringBuilder errors = new StringBuilder("Found "+errorList.size()+" errors: ");
//						for (ErrorRecord errorRecord : errorList) {
//							errors.append(errorRecord.getCode());
//						}
//						step.setMessage(errors.toString());
//					}
				} catch (Exception e) {
					log.error("Failed validation of file " + xmlPath + " by rule " + ruleDocumentValidation, e);
					step.setMessage(e.getMessage());
				}

				break;
			}
		} finally {
			step.done();
		}

		return step;
	}
}
