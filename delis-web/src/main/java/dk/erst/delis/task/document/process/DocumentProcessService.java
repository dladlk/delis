package dk.erst.delis.task.document.process;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXParseException;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentFormatFamily;
import dk.erst.delis.data.RuleDocumentTransformation;
import dk.erst.delis.data.RuleDocumentValidation;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.XSLTUtil;
import dk.erst.delis.task.document.process.validate.SchemaValidator;
import dk.erst.delis.task.document.process.validate.SchematronValidator;
import dk.erst.delis.task.document.process.validate.result.ISchematronResultCollector;
import dk.erst.delis.task.document.process.validate.result.SchematronResultCollectorFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentProcessService {

	private final RuleService ruleService;
	private final ConfigBean config;
	private DocumentParseService documentParseService;

	@Autowired
	public DocumentProcessService(RuleService ruleService, DocumentParseService documentParseService, ConfigBean config) {
		this.ruleService = ruleService;
		this.documentParseService = documentParseService;
		this.config = config;
	}

	public DocumentProcessLog process(Document document, Path xmlPath) {
		DocumentFormat ingoingDocumentFormat = document.getIngoingDocumentFormat();
		DocumentProcessLog plog = new DocumentProcessLog();
		try {
			processAllFormats(plog, xmlPath, ingoingDocumentFormat);
		} catch (Exception e) {
			log.error("Failed to process all formats on document " + document + " by path " + xmlPath, e);
		}

		return plog;
	}

	private Path processAllFormats(DocumentProcessLog plog, Path xmlPath, DocumentFormat documentFormat) {
		List<RuleDocumentValidation> ruleByFormat = ruleService.getValidationRuleListByFormat(documentFormat);
		for (RuleDocumentValidation ruleDocumentValidation : ruleByFormat) {
			DocumentProcessStep step = validateByRule(xmlPath, ruleDocumentValidation);
			plog.addStep(step);
			if (!step.isSuccess()) {
				return null;
			}
		}

		if (documentFormat.getDocumentFormatFamily().isLast()) {
			return xmlPath;
		}

		DocumentFormatFamily formatFamily = documentFormat.getDocumentFormatFamily();
		RuleDocumentTransformation transformationRule = ruleService.getTransformation(formatFamily);
		Path xmlOutPath = null;
		if (transformationRule != null) {
			String prefix = "transformation_" + formatFamily + "_to_" + transformationRule.getDocumentFormatFamilyTo() + "_";
			xmlOutPath = createTempFile(plog, xmlOutPath, prefix);
			if (xmlOutPath == null) {
				return null;
			}
			DocumentProcessStep step = transformByRule(xmlPath, xmlOutPath, transformationRule);
			plog.addStep(step);
			if (!step.isSuccess()) {
				return null;
			}
		}

		DocumentFormat resultFormat = identifyResultFormat(plog, xmlOutPath);

		return processAllFormats(plog, xmlOutPath, resultFormat);
	}

	private Path createTempFile(DocumentProcessLog plog, Path xmlOutPath, String prefix) {
		DocumentProcessStep step = new DocumentProcessStep("Create temp file with prefix " + prefix);
		try {
			xmlOutPath = Files.createTempFile(prefix, ".xml");
			step.setSuccess(true);
		} catch (Exception e) {
			log.error("Failed to create temp file", e);
			plog.addStep(step);
			return null;
		} finally {
			step.done();
		}
		return xmlOutPath;
	}

	protected DocumentFormat identifyResultFormat(DocumentProcessLog plog, Path xmlPath) {
		DocumentProcessStep step = new DocumentProcessStep("Define format of " + xmlPath);
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

		Path xslFilePath = filePath(transformationRule);
		try (FileInputStream xslStream = new FileInputStream(xslFilePath.toFile());
				FileInputStream xmlStream = new FileInputStream(xmlPath.toFile());
				FileOutputStream resultStream = new FileOutputStream(xmlOutPath.toFile())) {
			XSLTUtil.apply(xslStream, xslFilePath, xmlStream, resultStream);
			step.setSuccess(true);
		} catch (Exception e) {
			log.error("Failed to transform " + xmlPath + " with " + transformationRule, e);
			step.setMessage(e.getMessage());
		} finally {
			step.done();
		}

		return step;
	}

	protected Path filePath(RuleDocumentValidation r) {
		Path path = config.getStorageValidationPath().resolve(r.getRootPath());
		log.debug("Built validation path " + path);
		return path;
	}

	protected Path filePath(RuleDocumentTransformation r) {
		Path path = config.getStorageTransformationPath().resolve(r.getRootPath());
		log.debug("Built transformation path " + path);
		return path;
	}

	protected DocumentProcessStep validateByRule(Path xmlPath, RuleDocumentValidation ruleDocumentValidation) {
		DocumentProcessStep step = new DocumentProcessStep(ruleDocumentValidation);

		try {
			switch (ruleDocumentValidation.getValidationType()) {
			case XSD:
				SchemaValidator xsdValidator = new SchemaValidator();
				try (InputStream xmlStream = new FileInputStream(xmlPath.toFile())) {
					xsdValidator.validate(xmlStream, filePath(ruleDocumentValidation));
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
				try (InputStream xmlStream = new FileInputStream(xmlPath.toFile()); InputStream schematronStream = new FileInputStream(filePath(ruleDocumentValidation).toFile())) {
					ISchematronResultCollector collector = SchematronResultCollectorFactory.getCollector(ruleDocumentValidation.getDocumentFormat());
					schValidator.validate(xmlStream, schematronStream, collector);
					step.setSuccess(true);
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
