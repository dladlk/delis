package dk.erst.delis.task.document.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentBytesStorageService {

	private static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmssSSS";
	
	private ConfigBean configBean;

	public DocumentBytesStorageService(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public Path getIngoingFormatPath(Document document) {
		return Paths.get(configBean.getStorageLoadedPath().toString(), document.getIngoingRelativePath());
	}

	public Path createTempFile(DocumentProcessLog plog, Path xmlOutPath, String prefix) {
		DocumentProcessStep step = new DocumentProcessStep("Create temp file with prefix " + prefix, DocumentProcessStepType.COPY);
		try {
			xmlOutPath = Files.createTempFile(prefix, ".xml");
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

	public String moveToLoaded(File file, File metadataFile, Document document) {
		String destSubPath = buildDestSubPath(document);

		Path destRoot = configBean.getStorageLoadedPath();
		if (document.getDocumentStatus().isLoadFailed()) {
			destRoot = configBean.getStorageFailedPath();
		}

		Path destPath = destRoot.resolve(destSubPath);

		if (!moveFile(file, destPath)) {
			return null;
		}

		if (metadataFile != null) {
			Path metadataDestPath = destPath.resolveSibling(destSubPath + "_metadata.xml");
			try {
				Files.move(metadataFile.toPath(), metadataDestPath);
			} catch (IOException e) {
				log.error("Failed to move metadafile " + file + " after parsing to " + metadataDestPath + ", skip it");
			}
		}
		return destSubPath;
	}

	private boolean moveFile(File file, Path destPath) {
		File destParentFolder = destPath.getParent().toFile();
		if (!destParentFolder.exists()) {
			if (!destParentFolder.mkdirs()) {
				log.error("Cannot create parent folders for "+destPath);
				return false;
			}
		}
		Path path = file.toPath();
		try {
			Files.move(path, destPath);
		} catch (IOException e) {
			log.error("Failed to move file " + file + " after parsing to " + destPath + ", skip it");
			return false;
		}
		return true;
	}

	private String buildDestSubPath(Document document) {
		String timestamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(Calendar.getInstance().getTime());
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp);
		sb.append("_");
		sb.append(document.getIngoingDocumentFormat());
		sb.append(".xml");
		return sb.toString();
	}

	public String moveToValid(Document document, Path dataPath) {
		Path destRoot = configBean.getStorageValidPath();
		Path destSubPath = Paths.get(document.getIngoingRelativePath()).getFileName();
		if (!moveFile(dataPath.toFile(), destRoot.resolve(destSubPath))) {
			return null;
		}
		return destSubPath.toString();
	}

	public String moveToFailed(Document document, Path dataPath) {
		Path destRoot = configBean.getStorageFailedPath();
		Path destSubPath = Paths.get(document.getIngoingRelativePath()).getFileName();
		if (!moveFile(dataPath.toFile(), destRoot.resolve(destSubPath))) {
			return null;
		}
		return destSubPath.toString();		
	}

	public String moveToDeliver(Document document, String deliverPath) {
		// sourceRoot - place, where validated files are stored
		// deliverPath - deliver path for organization
		Path sourceRoot = configBean.getStorageValidPath();
		Path documentName = Paths.get(document.getIngoingRelativePath()).getFileName();
		Path destPath = Paths.get(deliverPath).resolve(documentName);
		if (!moveFile(sourceRoot.resolve(documentName).toFile(), destPath)) {
			return null;
		}
		return destPath.toString();
	}
}
