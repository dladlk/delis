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
import java.util.Date;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.task.document.parse.DocumentParseService;
import dk.erst.delis.task.document.parse.data.DocumentInfo;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentLoadService {

	private static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmssSSS";

	private static final String METADATA_XML = "metadata.xml";

	private DocumentDaoRepository documentDaoRepository;

	private JournalDocumentDaoRepository journalDocumentDaoRepository;

	private DocumentParseService documentParseService;
	
	private DocumentBytesStorageService documentBytesStorageService;
	
	private IdentifierResolverService identifierResolverService;

	@Autowired
	public DocumentLoadService(DocumentDaoRepository documentDaoRepository, JournalDocumentDaoRepository journalDocumentDaoRepository, DocumentParseService documentParseService, DocumentBytesStorageService documentBytesStorageService, IdentifierResolverService identifierResolverService) {
		super();
		this.documentDaoRepository = documentDaoRepository;
		this.journalDocumentDaoRepository = journalDocumentDaoRepository;
		this.documentParseService = documentParseService;
		this.documentBytesStorageService = documentBytesStorageService;
		this.identifierResolverService = identifierResolverService;
	}

	public StatData loadFromInput(Path inputFolderPath) {
		final StatData statData = new StatData();
		
		try {
			Files.walkFileTree(inputFolderPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (attrs.isRegularFile()) {
						String fileName = file.getFileName().toString();
						if (fileName.endsWith(".xml") && !fileName.startsWith(METADATA_XML)) {
							Document d = loadFile(file);
							statData.increment(d == null ? null : d.getDocumentStatus().name());
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error("Failed to scan folder " + inputFolderPath, e);
		}
		
		return statData;
	}
	
	public Document loadFile(Path xmlFilePath) {
		long start = System.currentTimeMillis();
		log.info("Loading file " + xmlFilePath);
		try {
			Date createTime = Calendar.getInstance().getTime();
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

			Identifier identifier = null;
			if (info.getReceiver() != null) {
				identifier = identifierResolverService.resolve(info.getReceiver().getSchemeId(), info.getReceiver().getId());
			}
			if (identifier != null) {
				document.setReceiverIdentifier(identifier);
				document.setOrganisation(identifier.getOrganisation());
			}
			
			if (document.getReceiverIdentifier() == null) {
				document.setDocumentStatus(DocumentStatus.UNKNOWN_RECEIVER);
			}

			
			String destSubPath = documentBytesStorageService.moveToLoaded(file, metadataFilePath, document);
			if (destSubPath == null) {
				return null;
			}
			
			document.setIngoingRelativePath(destSubPath);
			documentDaoRepository.save(document);
			
			JournalDocument jd = new JournalDocument();
			jd.setDocument(document);
			jd.setOrganisation(document.getOrganisation());
			jd.setCreateTime(createTime);
			jd.setDurationMs(System.currentTimeMillis() - start);
			jd.setType(DocumentProcessStepType.LOAD);
			jd.setMessage("Load file from "+xmlFilePath);
			jd.setSuccess(true);

			journalDocumentDaoRepository.save(jd);
			
			return document;

		} finally {
			log.info("Done loading file " + xmlFilePath + " in " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	private Document buildDocument(DocumentInfo info, String messageId) {
		Document document = new Document();

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
			DocumentFormat documentFormat = documentParseService.defineDocumentFormat(info);
			document.setIngoingDocumentFormat(documentFormat);
			document.setDocumentType(documentFormat.getDocumentType());

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
