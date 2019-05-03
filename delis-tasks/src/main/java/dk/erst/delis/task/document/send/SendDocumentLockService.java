package dk.erst.delis.task.document.send;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendDocumentLockService {

	private SendDocumentDaoRepository sendDocumentDaoRepository;

	@Autowired
	public SendDocumentLockService(SendDocumentDaoRepository sendDocumentDaoRepository) {
		this.sendDocumentDaoRepository = sendDocumentDaoRepository;
	}

	public SendDocument findDocumentAndLock(SendDocumentStatus status, SendDocumentStatus newStatus) {
		return findDocumentAndLock(0, status, newStatus);
	}

	public int unlock(SendDocument sendDocument, SendDocumentStatus oldStatus, SendDocumentStatus newStatus) {
		return sendDocumentDaoRepository.updateDocumentStatus(sendDocument, newStatus, oldStatus);
	}

	private SendDocument findDocumentAndLock(int attemptIndex, SendDocumentStatus status, SendDocumentStatus newStatus) {
		SendDocumentStatus currentStatus = status;
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
		SendDocument document = sendDocumentDaoRepository.findTop1ByDocumentStatusOrderByIdAsc(currentStatus);
		if (document != null) {
			if (sendDocumentDaoRepository.updateDocumentStatus(document, newStatus, currentStatus) != 1) {
				return findDocumentAndLock(attemptIndex + 1, status, newStatus);
			}
		}
		if (document != null) {
			if (log.isDebugEnabled()) {
				log.debug("Found SendDocument #" + document.getId());
			}
		}
		return document;
	}
	
	public SendDocument findDocumentAndLock(SendDocumentStatus currentStatus, SendDocumentStatus newStatus, SendDocument example) {
		return findDocumentAndLock(0, currentStatus, newStatus, example);
	}
	
	private SendDocument findDocumentAndLock(int attemptIndex, SendDocumentStatus currentStatus, SendDocumentStatus newStatus, SendDocument example) {
		if (log.isDebugEnabled()) {
			log.debug("findAndLock " + attemptIndex);
		}
		if (attemptIndex > 5) {
			/*
			 * Potection again cicle
			 */
			log.error("SUSPICIOUS: findDocumentAndLock reached attempt index limit with value " + attemptIndex + ", skip further attempts");
			return null;
		}
		example.setDocumentStatus(currentStatus);
		SendDocument document = sendDocumentDaoRepository.findAll(Example.of(example), PageRequest.of(0, 1, Direction.ASC, "id")).iterator().next();
		if (document != null) {
			if (sendDocumentDaoRepository.updateLockedAndDocumentStatus(document, newStatus, currentStatus, true) != 1) {
				return findDocumentAndLock(attemptIndex + 1, currentStatus, newStatus);
			}
		}
		if (document != null) {
			if (log.isDebugEnabled()) {
				log.debug("Found SendDocument #" + document.getId());
			}
		}
		return document;
	}

}
