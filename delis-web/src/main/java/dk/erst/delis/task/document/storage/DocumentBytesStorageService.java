package dk.erst.delis.task.document.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentBytesStorageService {

	private ConfigBean configBean;
	private DocumentBytesDaoRepository documentBytesDaoRepository;

	@Autowired
	public DocumentBytesStorageService(ConfigBean configBean, DocumentBytesDaoRepository documentBytesDaoRepository) {
		this.configBean = configBean;
		this.documentBytesDaoRepository = documentBytesDaoRepository;
	}

	private File buildFile(DocumentBytes documentBytes) {
		Document document = documentBytes.getDocument();
		Organisation organisation = document.getOrganisation();
		String fileName = wrapZeros(documentBytes.getId(), 3) + "-" + documentBytes.getType()+".xml";
		
		Path rootFolder;
		if (organisation != null ) {
			rootFolder = Paths.get(configBean.getStorageLoadedPath().toString(), organisation.getCode());
		} else {
			rootFolder = configBean.getStorageFailedPath();
		}
		
		Path filePath = Paths.get(rootFolder.toString(), wrapZeros(document.getId(), 5), fileName);

		return filePath.toFile();
	}

	private String wrapZeros(Long id, int upToLength) {
		String s = String.valueOf(id);
		if (s.length() < upToLength) {
			s = "000000".substring(0, upToLength - s.length()) + s;
		}
		return s;
	}

	public boolean save(Document document, DocumentBytesType type, long fileSize, InputStream stream) {
		boolean result = true;

		DocumentBytes documentBytes = new DocumentBytes();
		documentBytes.setDocument(document);
		documentBytes.setType(type);
		documentBytes.setSize(fileSize);

		documentBytesDaoRepository.save(documentBytes);

		File file = buildFile(documentBytes);

		file.getParentFile().mkdirs();
		try {
			Files.copy(stream, file.toPath());
			log.debug("Saved " + fileSize + " bytes to " + file);
		} catch (IOException e) {
			log.error("Failed to save document bytes to " + file, e);
			result = false;
		}
		return result;
	}

	public boolean load(DocumentBytes documentBytes, OutputStream stream) {
		boolean result = true;
		Path path = buildFile(documentBytes).toPath();
		try {
			Files.copy(path, stream);
			log.debug("Loaded data from " + path);
		} catch (IOException e) {
			log.error("Failed to read document bytes from " + path, e);
			result = false;
		}
		return result;
	}

	public DocumentBytes find(Document document, DocumentBytesType in) {
		return documentBytesDaoRepository.findLastByDocumentAndType(document, DocumentBytesType.IN);
	}
}
