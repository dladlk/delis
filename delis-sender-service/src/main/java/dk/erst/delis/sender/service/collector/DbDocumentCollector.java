package dk.erst.delis.sender.service.collector;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.sender.service.document.DocumentData;
import dk.erst.delis.sender.service.document.IDocumentData;
import dk.erst.delis.task.document.storage.SendDocumentBytesStorageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "delis.sender.document.collector", havingValue = "db")
public class DbDocumentCollector implements IDocumentCollector {

	private SendDocumentDaoRepository sendDocumentDaoRepository;
	private SendDocumentBytesStorageService sendDocumentBytesStorageService;

	@Autowired
	public DbDocumentCollector(SendDocumentDaoRepository sendDocumentDaoRepository, SendDocumentBytesStorageService sendDocumentBytesStorageService) {
		this.sendDocumentDaoRepository = sendDocumentDaoRepository;
	}

	@Override
	public IDocumentData findDocument() {
		SendDocument sendDocument = findAndLock(0);
		if (sendDocument != null) {
			SendDocumentBytes documentBytes = sendDocumentBytesStorageService.find(sendDocument, SendDocumentBytesType.ORIGINAL);
			if (documentBytes == null) {
				log.error("SUSPICOUS: Cannot find document bytes for document " + sendDocument);
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (!sendDocumentBytesStorageService.load(documentBytes, baos)) {
					log.error("SUSPICOUS: documents bytes by " + documentBytes + " cannot be loaded");
				} else {
					DocumentData documentData = new DocumentData();
					documentData.setData(baos.toByteArray());
					documentData.setDescription("SendDocument#" + sendDocument.getId());
					return documentData;
				}
			}
		}

		return null;
	}

	private SendDocument findAndLock(int attemptIndex) {
		if (log.isDebugEnabled()) {
			log.debug("findAndLock " + attemptIndex);
		}
		if (attemptIndex > 5) {
			/*
			 * Potection again cicle
			 */
			log.error("SUSPICIOUS: findSendDocumentWithValidStatus reached attempt index limit with value " + attemptIndex + ", skip further attempts");
			return null;
		}
		SendDocument document = sendDocumentDaoRepository.findTop1ByDocumentStatusOrderByIdAsc(SendDocumentStatus.VALID);
		if (document != null) {
			if (sendDocumentDaoRepository.updateDocumentStatus(document, SendDocumentStatus.SEND_START, SendDocumentStatus.VALID) != 1) {
				return findAndLock(attemptIndex + 1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Found " + document.getId());
		}
		return document;
	}
}
