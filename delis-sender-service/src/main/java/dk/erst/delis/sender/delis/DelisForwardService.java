package dk.erst.delis.sender.delis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.sender.service.SendService.SendFailureType;
import dk.erst.delis.task.organisation.OrganisationService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;

@Service
public class DelisForwardService {

	private OrganisationSetupService organisationSetupService;
	private OrganisationService organisationService;

	@Autowired
	public DelisForwardService(OrganisationSetupService organisationSetupService, OrganisationService organisationService) {
		this.organisationSetupService = organisationSetupService;
		this.organisationService = organisationService;
	}

	public static enum ForwardResult {

		ERST_NOT_FOUND,

		OK,
	}

	public ForwardResult forward(DelisDocumentData documentData, SendFailureType failureType) {
		SendDocument sendDocument = documentData.getSendDocument();
		OrganisationSetupData setupData = organisationSetupService.load(sendDocument.getOrganisation());
		if (setupData != null) {
			if (setupData.isSendUndeliverableInvoiceResponseToERST()) {
				Organisation erst = organisationService.findOrganisationByCode("erst");
				if (erst == null) {
					return ForwardResult.ERST_NOT_FOUND;
				}
			}
		}
		return ForwardResult.OK;
	}
}
