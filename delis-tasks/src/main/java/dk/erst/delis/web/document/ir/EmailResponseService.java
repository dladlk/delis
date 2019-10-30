package dk.erst.delis.web.document.ir;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.task.document.response.ApplicationResponseService.MessageLevelLineResponse;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;

@Service
public class EmailResponseService {

	@Autowired
	private OrganisationSetupService organisationSetupService;

	public EmailResponseForm buildEmailResponse(Document document, MessageLevelResponseForm mlrForm) {
		Organisation organisation = document.getOrganisation();

		OrganisationSetupData setupData = organisationSetupService.load(organisation);

		EmailResponseForm f = new EmailResponseForm();
		f.setDocumentId(document.getId());
		StringBuilder s = new StringBuilder();
		s.append("[DELIS] ");
		s.append("[#");
		s.append(document.getId());
		s.append("] Validation error in ");
		s.append(document.getDocumentType().getName());
		s.append(" ");
		s.append(document.getDocumentId());
		if (document.getOrganisation() != null) {
			s.append(" to ");
			s.append(document.getOrganisation().getName());
		}
		
		f.setSubject(s.toString());
		
		if (setupData != null && !StringUtils.isEmpty(setupData.getOnErrorSenderEmailAddress())) {
			f.setFrom(setupData.getOnErrorSenderEmailAddress());
		}
		if (setupData != null && !StringUtils.isEmpty(setupData.getOnErrorReceiverEmailAddress())) {
			f.setTo(setupData.getOnErrorReceiverEmailAddress());
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Dear DELIS support,\n\n");

		sb.append(document.getDocumentType().getName());
		sb.append(" ");
		sb.append(document.getDocumentId());
		sb.append(", issued ");
		sb.append(document.getDocumentDate());
		sb.append(" to ");
		sb.append(document.getReceiverIdRaw());
		sb.append(" failed validation.");
		sb.append("\n\n");

		if (mlrForm.getDescription() != null) {
			sb.append(mlrForm.getDescription());
			sb.append("\n\n");
		}

		sb.append("Please review the issue and inform sender party of the document.\n\n");
		
		sb.append("Information about sender:\n\n");
		if (StringUtils.isNotBlank(document.getSenderName())) {
			sb.append("Name: ");
			sb.append(document.getSenderName());
			sb.append("\n");
		}
		if (StringUtils.isNotBlank(document.getSenderIdRaw())) {
			sb.append("Identifier: ");
			sb.append(document.getSenderIdRaw());
			sb.append("\n");
		}
		if (StringUtils.isNotBlank(document.getSenderCountry())) {
			sb.append("Country: ");
			sb.append(document.getSenderCountry());
			sb.append("\n");
		}
		if (StringUtils.isNotBlank(document.getSenderEmail())) {
			sb.append("Document contact email: ");
			sb.append(document.getSenderEmail());
			sb.append("\n");
		}
		sb.append("\n\n");		

		List<MessageLevelLineResponse> lineResponseList = mlrForm.getLineResponseList();
		if (lineResponseList != null) {
			for (int i = 0; i < lineResponseList.size(); i++) {
				MessageLevelLineResponse line = lineResponseList.get(i);

				sb.append(i + 1);
				sb.append(". ");
				sb.append(line.getReasonCode());
				sb.append(": ");
				sb.append(line.getDescription());
				sb.append("\n");
				sb.append("at ");
				sb.append(line.getLineId());
				sb.append("\n\n");
			}
		}
		
		sb.append("Kind regards,\n");
		sb.append(f.getFrom());

		f.setBody(sb.toString());

		return f;
	}
}
