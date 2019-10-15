package dk.erst.delis.task.document.deliver;

import java.util.List;

import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentExportDaoRepository;
import dk.erst.delis.data.entities.document.DocumentExport;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentExportStatus;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentCheckDeliveryService {

    private DocumentExportDaoRepository documentExportDaoRepository;
    private OrganisationSetupService organisationSetupService;

	
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
                    boolean presentValidated;
                    Long previousDocumentId = 0l;
                    do {
                        List<DocumentExport> list = documentExportDaoRepository.findForExportCheck(status, org, previousDocumentId);
                        presentValidated = !list.isEmpty();

                        for (DocumentExport documentExport : list) {
                            previousDocumentId = documentExport.getId();
                            checkExportDocument(statData, documentExport, setupData);
                        }
                    } while (presentValidated);
                }
            }
        } finally {
            log.info("Done checking status of exported documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
        }
	}
	
    
    protected boolean checkExportDocument(StatData statData, DocumentExport documentExport, OrganisationSetupData setupData) {
        statData.increment("OK");
        /*
         * TODO: Implement check and update of DocumentExport status
         */
        return true;
    }	
}
