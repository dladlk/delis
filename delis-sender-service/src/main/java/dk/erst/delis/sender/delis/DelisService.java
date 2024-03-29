package dk.erst.delis.sender.delis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.JournalSendDocumentDaoRepository;
import dk.erst.delis.dao.SendDocumentDaoRepository;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.data.enums.document.SendDocumentProcessStepType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import dk.erst.delis.oxalis.sender.response.DelisResponse;
import dk.erst.delis.sender.delis.DelisForwardService.ForwardResult;
import dk.erst.delis.sender.document.IDocumentData;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import dk.erst.delis.task.document.send.SendDocumentLockService;
import dk.erst.delis.task.document.storage.SendDocumentBytesStorageService;

@Service
public class DelisService {

	private SendDocumentDaoRepository sendDocumentDaoRepository;
	private SendDocumentBytesStorageService sendDocumentBytesStorageService;
	private JournalSendDocumentDaoRepository journalSendDocumentDaoRepository;
	private SendDocumentLockService sendDocumentLockService;
	
	private DelisForwardService delisForwardService = null;

	@Autowired
	public DelisService(SendDocumentDaoRepository sendDocumentDaoRepository, SendDocumentBytesStorageService sendDocumentBytesStorageService, JournalSendDocumentDaoRepository journalSendDocumentDaoRepository, SendDocumentLockService sendDocumentLockService) {
		this.sendDocumentDaoRepository = sendDocumentDaoRepository;
		this.sendDocumentBytesStorageService = sendDocumentBytesStorageService;
		this.journalSendDocumentDaoRepository = journalSendDocumentDaoRepository;
		this.sendDocumentLockService = sendDocumentLockService;
	}

	public SendDocumentBytes findBytes(SendDocument sendDocument) {
		return sendDocumentBytesStorageService.find(sendDocument, SendDocumentBytesType.ORIGINAL);
	}

	public boolean loadBytes(SendDocumentBytes documentBytes, ByteArrayOutputStream baos) {
		return sendDocumentBytesStorageService.load(documentBytes, baos);
	}

	public void createStartSendJournal(SendDocument sendDocument, long durationMs) {
		JournalSendDocument j = new JournalSendDocument();
		j.setDocument(sendDocument);
		j.setMessage(trunc("Start send attempt via PEPPOL"));
		j.setOrganisation(sendDocument.getOrganisation());
		j.setType(SendDocumentProcessStepType.SEND);
		j.setSuccess(true);
		j.setDurationMs(durationMs);
		journalSendDocumentDaoRepository.save(j);
	}

	public void createSentJournal(IDocumentData documentData, DelisResponse response) {
		SendDocument sendDocument = ((DelisDocumentData) documentData).getSendDocument();
		JournalSendDocument j = new JournalSendDocument();
		j.setDocument(sendDocument);
		j.setMessage(trunc("Delivered to endpoint " + response.getEndpoint().getAddress() + " via " + response.getEndpoint().getTransportProfile().getIdentifier()));
		j.setOrganisation(sendDocument.getOrganisation());
		j.setType(SendDocumentProcessStepType.SEND);
		j.setSuccess(true);
		j.setDurationMs(System.currentTimeMillis() - documentData.getStartTime());
		journalSendDocumentDaoRepository.save(j);
	}

	public void createFailureJournal(IDocumentData documentData, SendFailureType failureType, Throwable e) {
		SendDocument sendDocument = ((DelisDocumentData) documentData).getSendDocument();

		JournalSendDocument j = new JournalSendDocument();
		j.setDocument(sendDocument);
		j.setMessage(trunc("Failed PEPPOL delivery with type " + failureType + " and message " + e.getMessage()));
		j.setOrganisation(sendDocument.getOrganisation());
		j.setType(SendDocumentProcessStepType.SEND);
		j.setSuccess(false);
		j.setDurationMs(System.currentTimeMillis() - documentData.getStartTime());
		journalSendDocumentDaoRepository.save(j);
	}

	private void createForwardJournal(SendDocument sendDocument, String message, boolean success, long duration) {
		JournalSendDocument j = new JournalSendDocument();
		j.setDocument(sendDocument);
		j.setMessage(trunc(message));
		j.setOrganisation(sendDocument.getOrganisation());
		j.setType(SendDocumentProcessStepType.FORWARD);
		j.setSuccess(success);
		j.setDurationMs(duration);
		journalSendDocumentDaoRepository.save(j);
	}

	public SendDocument findDocumentAndLock(SendDocumentStatus status, SendDocumentStatus newStatus) {
		return sendDocumentLockService.findDocumentAndLock(status, newStatus);
	}

	public boolean markDocumentSent(IDocumentData documentData, String messageId, Date deliveredDate) {
		DelisDocumentData d = (DelisDocumentData) documentData;
		return sendDocumentDaoRepository.markDocumentSent(d.getSendDocument(), messageId, deliveredDate) == 1;
	}

	public boolean markDocumentFailed(IDocumentData documentData) {
		DelisDocumentData d = (DelisDocumentData) documentData;
		return sendDocumentDaoRepository.updateDocumentStatus(d.getSendDocument(), SendDocumentStatus.SEND_ERROR, SendDocumentStatus.SEND_START) == 1;
	}

	private String trunc(String string) {
		return StringUtils.truncate(string, 250);
	}

	public void saveReceipt(IDocumentData documentData, byte[] receipt) {
		SendDocument sendDocument = ((DelisDocumentData) documentData).getSendDocument();
		sendDocumentBytesStorageService.save(sendDocument, SendDocumentBytesType.RECEIPT, receipt.length, new ByteArrayInputStream(receipt));
	}

	public void failurePostProcess(IDocumentData documentData, SendFailureType failureType) {
		DelisDocumentData d = (DelisDocumentData) documentData;
		SendDocument sendDocument = d.getSendDocument();
		if (sendDocument.getDocumentType().isResponse() && sendDocument.getOrganisation() != null) {
			long start = System.currentTimeMillis();
			ForwardResult forwardResult = delisForwardService.forward(d, failureType);
			long duration = System.currentTimeMillis() - start;

			String message = null;
			boolean success = false;
			switch (forwardResult) {
			case ERST_NOT_FOUND:
				message = "Organisation is configured to forward undeliverable InvoiceResponse to ERST, but organisation with code \"erst\" is not found.";
				break;
			case OK:
				message = "Organisation is configured to forward undeliverable InvoiceResponse to ERST, but organisation with code \"erst\" is not found.";
				success = true;
				break;
			}
			if (message != null) {
				createForwardJournal(sendDocument, message, success, duration);
			}
		}
	}
}
