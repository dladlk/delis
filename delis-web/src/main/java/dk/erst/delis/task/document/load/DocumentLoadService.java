package dk.erst.delis.task.document.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.DocumentRepository;
import dk.erst.delis.data.Document;
import dk.erst.delis.data.DocumentStatus;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentLoadService {

	private static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmssSSS";

	private static final String METADATA_XML = "metadata.xml";

	private DocumentRepository documentRepository;

	private DocumentParseService documentParseService;

	private ConfigBean config;

	@Autowired
	public DocumentLoadService(DocumentRepository documentRepository, DocumentParseService documentParseService, ConfigBean config) {
		super();
		this.documentRepository = documentRepository;
		this.documentParseService = documentParseService;
		this.config = config;
	}

	public void loadFromInput() {
		final Path inputFolderPath = config.getStorageInputPath();

		try {
			Files.walkFileTree(inputFolderPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (attrs.isRegularFile()) {
						Path fileName = file.getFileName();
						if (fileName.endsWith(".xml") && !fileName.startsWith(METADATA_XML)) {
							loadFile(file);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error("Failed to scan folder " + inputFolderPath, e);
		}
	}

	protected Document loadFile(Path xmlFilePath) {
		long start = System.currentTimeMillis();
		log.info("Loading file " + xmlFilePath);
		try {
			File file = xmlFilePath.toFile();
			Path xmlFileParentPath = xmlFilePath.getParent();
			if (!file.exists()) {
				log.error("File " + xmlFilePath + " does not exist");
				return null;
			}
			File fileLoad = Paths.get(xmlFilePath.toString() + ".load").toFile();
			if (!file.renameTo(fileLoad)) {
				log.warn("Cannot rename file " + xmlFilePath + " before loading, skip it");
				return null;
			} else {
				log.info("Renamed file "+file+" to .load before processing");
				file = fileLoad;
			}

			DocumentInfo info = parseDocumentInfo(file);

			File metadataFilePath = findMetadataFile(xmlFileParentPath);
			String messageId = parseMessageId(xmlFileParentPath, metadataFilePath, info);

			Document document = buildDocument(info, messageId);

			String destSubPath = moveToLoaded(file, metadataFilePath, document);
			if (destSubPath == null) {
				return null;
			}
			
			document.setIngoingRelativePath(destSubPath);
			documentRepository.save(document);

			return document;

		} finally {
			log.info("Done loading file " + xmlFilePath + " in " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	private String moveToLoaded(File file, File metadataFile, Document document) {
		String destSubPath = buildDestSubPath(document);

		Path destRoot = config.getStorageLoadedPath();
		if (document.getDocumentStatus().isLoadFailed()) {
			destRoot = config.getStorageFailedPath();
		}

		Path destPath = destRoot.resolve(destSubPath);

		File destParentFolder = destPath.getParent().toFile();
		if (!destParentFolder.exists()) {
			if (!destParentFolder.mkdirs()) {
				log.error("Cannot create parent folders for "+destParentFolder);
				return null;
			}
		}
		try {
			Files.move(file.toPath(), destPath);
		} catch (IOException e) {
			log.error("Failed to move file " + file + " after parsing to " + destPath + ", skip it");
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

	private String buildDestSubPath(Document document) {
		String timestamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(Calendar.getInstance().getTime());
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp);
		sb.append("_");
		sb.append(document.getIngoingDocumentFormat());
		sb.append(".xml");
		return sb.toString();
	}

	private Document buildDocument(DocumentInfo info, String messageId) {
		Document document = new Document();

		document.setOrganisation(null);
		document.setReceiverIdentifier(null);

		if (info != null) {
			document.setDocumentDate(info.getDate());
			document.setDocumentId(info.getId());
			if (info.getReceiver() != null) {
				document.setReceiverIdRaw(info.getReceiver().encodeID());
				document.setReceiverCountry(info.getReceiver().getCountry());
				document.setReceiverName(info.getReceiver().getName());
			}
			if (info.getSender() != null) {
				document.setSenderIdRaw(info.getSender().encodeID());
				document.setSenderCountry(info.getSender().getCountry());
				document.setSenderName(info.getSender().getName());
			}
			document.setIngoingDocumentFormat(documentParseService.defineDocumentFormat(info));

			if (!document.getIngoingDocumentFormat().isUnsupported()) {
				document.setDocumentStatus(DocumentStatus.LOAD_OK);
			}
		} else {
			document.setDocumentStatus(DocumentStatus.LOAD_ERROR);
		}
		document.setMessageId(messageId);
		return document;
	}

	private DocumentInfo parseDocumentInfo(File file) {
		InputStream is = null;
		DocumentInfo header = null;
		try {
			is = new FileInputStream(file);
			header = documentParseService.parseDocumentInfo(is);
			log.info("Parsed "+header);
		} catch (Exception e) {
			log.error("Failed to parse document info on file " + file, e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		return header;
	}

	protected String parseMessageId(Path xmlFileParentPath, File metadataFilePath, DocumentInfo header) {
		if (metadataFilePath != null) {
			return xmlFileParentPath.getFileName().toString();
		}
		return "fake-" + new SimpleDateFormat(TIMESTAMP_FORMAT).format(Calendar.getInstance().getTime());
	}

	protected File findMetadataFile(Path xmlFileParentPath) {
		Path metadataFile = xmlFileParentPath.resolve(METADATA_XML);
		if (!metadataFile.toFile().exists()) {
			log.info("Metadata file is not found");
			return null;
		}
		return metadataFile.toFile();
	}

}
