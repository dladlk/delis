package dk.erst.delis.sender.service.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.sender.service.dao.SendDocumentBytesDaoRepository;
import dk.erst.delis.sender.service.dao.SendDocumentDaoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "delis.sender.document.collector", havingValue = "db")
public class DbDocumentCollector implements IDocumentCollector {

	private SendDocumentDaoRepository sendDocumentDaoRepository;
	private SendDocumentBytesDaoRepository sendDocumentBytesDaoRepository;

	@Autowired
	public DbDocumentCollector(SendDocumentDaoRepository sendDocumentDaoRepository, SendDocumentBytesDaoRepository sendDocumentBytesDaoRepository) {
		this.sendDocumentDaoRepository = sendDocumentDaoRepository;
		this.sendDocumentBytesDaoRepository = sendDocumentBytesDaoRepository;
	}
	
	@Override
	public IDocumentData findDocument() {
		SendDocument sendDocument = findAndLock(0);
		if (sendDocument != null) {
			SendDocumentBytes documentBytes = sendDocumentBytesDaoRepository.findTop1ByDocumentAndTypeOrderByIdDesc(sendDocument, SendDocumentBytesType.ORIGINAL);
			if (documentBytes == null) {
				log.error("SUSPICOUS: Cannot find document bytes for document " + sendDocument);
			} else {

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
