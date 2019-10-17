package dk.erst.delis.task.document.deliver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.DocumentExportDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentExport;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentExportStatus;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.vfs.service.VFSService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentCheckDeliveryService {

	private DocumentExportDaoRepository documentExportDaoRepository;
	private DocumentDaoRepository documentDaoRepository;
	private OrganisationSetupService organisationSetupService;
	private VFSService vfsService;
	private JournalDocumentService journalDocumentService; 

	@Autowired
	public DocumentCheckDeliveryService(DocumentExportDaoRepository documentExportDaoRepository, DocumentDaoRepository documentDaoRepository, OrganisationSetupService organisationSetupService, VFSService vfsService, JournalDocumentService journalDocumentService) {
		this.documentExportDaoRepository = documentExportDaoRepository;
		this.documentDaoRepository = documentDaoRepository;
		this.organisationSetupService = organisationSetupService;
		this.vfsService = vfsService;
		this.journalDocumentService = journalDocumentService;
	}

	public StatData checkDelivery() {
		StatData statData = new StatData();
		checkDelivery(DocumentExportStatus.EXPORTED, statData);
		checkDelivery(DocumentExportStatus.PENDING, statData);
		return statData;
	}

	private void checkDelivery(DocumentExportStatus status, StatData statData) {
		try {

			List<Organisation> organisations = documentExportDaoRepository.loadDocumentExportStatusStat(status);
			for (Organisation org : organisations) {

				OrganisationSetupData setupData = organisationSetupService.load(org);
				if (setupData.getReceivingMethod() == null) {
					log.info("No recieving method defined for organization " + org.getName() + ". Documents delivery check skipped");
					statData.increment("Organizations with no recieving method");
				} else {
					boolean found;
					Long previousDocumentId = 0l;
					do {
						List<DocumentExport> list = documentExportDaoRepository.findForExportCheck(status, org, previousDocumentId, PageRequest.of(0, 2));
						found = !list.isEmpty();

						for (DocumentExport documentExport : list) {
							previousDocumentId = documentExport.getId();
							checkExportDocument(statData, documentExport, setupData);
						}
					} while (found);
				}
			}
		} finally {
			log.info("Done checking status of exported documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
		}
	}

	protected boolean checkExportDocument(StatData statData, DocumentExport documentExport, OrganisationSetupData setupData) {
		try {
			String outputFileName = documentExport.getExportFileName(); 
			DocumentProcessStep step = new DocumentProcessStep("Check delivery of "+outputFileName, DocumentProcessStepType.CHECK_DELIVERY);
			
			OrganisationReceivingMethod receivingMethod = setupData.getReceivingMethod();
			String receivingMethodSetup = setupData.getReceivingMethodSetup();
			
			boolean fileExists = false;
			
			documentExport.getDocument();
			
			switch (receivingMethod) {
			    case FILE_SYSTEM:
			        File outputFile = new File(receivingMethodSetup, outputFileName);
			        fileExists = outputFile.exists();
			        break;
			    case VFS:
			    	fileExists = vfsService.exist(receivingMethodSetup, "/"+outputFileName);
			        break;
			    default:
			    	statData.increment("Unrecognized receiving method "+receivingMethod);
			    	return true;
			}
			
			documentExport.setLastCheckDate(Calendar.getInstance().getTime());
			Document document = documentExport.getDocument();
			String incrementMessage;
			
			if (fileExists) {
				documentExport.setStatus(DocumentExportStatus.PENDING);
				
				if (document.getDocumentStatus() == DocumentStatus.EXPORT_OK) {
					/*
					 * Switch Document to DELIVER_PENDING if it is not delivered more than some time
					 */
					
					Calendar c = Calendar.getInstance();
					int alertAfterMins = setupData.getCheckDeliveredAlertMins();
					c.add(Calendar.MINUTE, -1 * alertAfterMins);
					if (c.getTime().after(documentExport.getExportDate())) {
						document.setDocumentStatus(DocumentStatus.DELIVER_PENDING);
						documentDaoRepository.updateDocumentStatus(document);
						
						incrementMessage = "PENDING_WARN_NEW";

						step.setMessage("still pending");
						step.setSuccess(true);
					} else {
						incrementMessage = "PENDING";
					}
				} else {
					incrementMessage = "PENDING_WARN_OLD";
				}
			} else {
				documentExport.setStatus(DocumentExportStatus.DELIVERED);
				documentExport.setDeliveredDate(documentExport.getLastCheckDate());
				document.setDocumentStatus(DocumentStatus.DELIVER_OK);
				documentDaoRepository.updateDocumentStatus(document);
			
				step.setMessage("consumed");
				step.setSuccess(true);
				
				incrementMessage = "DELIVERED";
			}
			statData.increment(incrementMessage);
			documentExportDaoRepository.save(documentExport);

			if (step != null && step.isSuccess()) {
				step.done();
				List<DocumentProcessStep> stepList = new ArrayList<DocumentProcessStep>();
				stepList.add(step);
				journalDocumentService.saveDocumentStep(document, stepList);
			}
			
			return true;
		} catch (Exception e) {
			log.error("Failed to check delivery status for document export "+documentExport, e);
			statData.increment("FAILURE");
			return false;
		}
	}
}
