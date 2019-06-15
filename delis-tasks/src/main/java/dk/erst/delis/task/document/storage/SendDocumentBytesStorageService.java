package dk.erst.delis.task.document.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.SendDocumentBytesDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendDocumentBytesStorageService {

	private ConfigBean configBean;
	private SendDocumentBytesDaoRepository documentBytesDaoRepository;

	@Autowired
	public SendDocumentBytesStorageService(ConfigBean configBean, SendDocumentBytesDaoRepository sendDocumentBytesDaoRepository) {
		this.configBean = configBean;
		this.documentBytesDaoRepository = sendDocumentBytesDaoRepository;
	}

	private File buildFile(SendDocumentBytes documentBytes) {
		SendDocument document = documentBytes.getDocument();
		Organisation organisation = document.getOrganisation();
		String fileName = wrapZeros(documentBytes.getId(), 3) + "-" + documentBytes.getType() + ".xml";

		Path rootFolder;
		String subFolder;
		if (organisation != null) {
			subFolder = organisation.getCode();
		} else {
			subFolder = "_";
		}
		rootFolder = Paths.get(configBean.getStorageSendPath().toString(), subFolder);

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

	public boolean save(SendDocument document, SendDocumentBytesType type, long fileSize, InputStream stream) {
		boolean result = true;

		SendDocumentBytes documentBytes = new SendDocumentBytes();
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

	public boolean load(SendDocumentBytes documentBytes, OutputStream stream) {
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

	public SendDocumentBytes find(SendDocument document, SendDocumentBytesType type) {
		return documentBytesDaoRepository.findTop1ByDocumentAndTypeOrderByIdDesc(document, type);
	}

	public SendDocumentBytes find(long documentId, long id) {
		return documentBytesDaoRepository.findByDocumentIdAndId(documentId, id);
	}

	public List<SendDocumentBytes> findAll(SendDocument document) {
		return documentBytesDaoRepository.findByDocument(document);
	}

}
