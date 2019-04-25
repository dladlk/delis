package dk.erst.delis.sender.delis;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.sender.collector.IDocumentCollector;
import dk.erst.delis.sender.document.IDocumentData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "delis.sender.document.collector", havingValue = "delis")
public class DelisDocumentCollector implements IDocumentCollector {

	private DelisService dbService;
	
	@Autowired
	public DelisDocumentCollector(DelisService dbService) {
		this.dbService = dbService;
	}

	@Override
	public IDocumentData findDocument() {
		long start = System.currentTimeMillis();
		SendDocument sendDocument = dbService.findDocumentAndLock(0);
		if (sendDocument != null) {
			SendDocumentBytes documentBytes = dbService.findBytes(sendDocument);
			if (documentBytes == null) {
				log.error("SUSPICOUS: Cannot find document bytes for document " + sendDocument);
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (!dbService.loadBytes(documentBytes, baos)) {
					log.error("SUSPICOUS: documents bytes by " + documentBytes + " cannot be loaded");
				} else {
					DelisDocumentData documentData = new DelisDocumentData(sendDocument.getId());
					documentData.setData(baos.toByteArray());
					documentData.setDescription("SendDocument#" + sendDocument.getId());
					documentData.setSendDocument(sendDocument);

					long durationMs = System.currentTimeMillis() - start;
					dbService.createStartSendJournal(sendDocument, durationMs);
					
					return documentData;
				}
			}
		}

		return null;
	}

}
