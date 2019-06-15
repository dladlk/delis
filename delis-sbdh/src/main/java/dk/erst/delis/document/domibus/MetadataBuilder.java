package dk.erst.delis.document.domibus;

import eu.domibus.plugin.fs.ebms3.*;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.util.List;

public class MetadataBuilder {

	public static String DEFAULT_PROCESS_SCHEME_ID = "urn:fdc:peppol.eu:2017:identifiers:proc-id";

	public UserMessage buildUserMessage(Header sbdhHeader, String partyIdValue) {
		UserMessage userMessage = new UserMessage();
		PartyInfo partyInfo = new PartyInfo();
		From from = new From();
		PartyId partyId = new PartyId();
		partyId.setType("urn:fdc:peppol.eu:2017:identifiers:ap");
		partyId.setValue(partyIdValue);
		from.setPartyId(partyId);
		from.setRole("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator");
		partyInfo.setFrom(from);

		AgreementRef agreementRef = new AgreementRef();
		agreementRef.setValue("urn:fdc:peppol.eu:2017:agreements:tia:ap_provider");
		Service service = new Service();
//		service.setType(sbdhHeader.getProcess().getScheme().getIdentifier());
//		service.setType("urn:fdc:peppol.eu:2017:identifiers:proc-id");
		service.setType(DEFAULT_PROCESS_SCHEME_ID);
		service.setValue(sbdhHeader.getProcess().getIdentifier());
		CollaborationInfo collaborationInfo = new CollaborationInfo();
		collaborationInfo.setAgreementRef(agreementRef);
		collaborationInfo.setService(service);
		collaborationInfo.setAction(sbdhHeader.getDocumentType().toString());

		Property originalSender = new Property();
		originalSender.setName("originalSender");
		ParticipantIdentifier sender = sbdhHeader.getSender();
		ParticipantIdentifier receiver = sbdhHeader.getReceiver();
		originalSender.setType(sender.getScheme().getIdentifier());
		originalSender.setValue(sender.getIdentifier());

		Property finalRecipient = new Property();
		finalRecipient.setName("finalRecipient");
		finalRecipient.setType(receiver.getScheme().getIdentifier());
		finalRecipient.setValue(receiver.getIdentifier());

		Property mimeType = new Property();
		mimeType.setName("mimeType");
		mimeType.setValue("text/xml");

		MessageProperties messageProperties = new MessageProperties();
		List<Property> propertyList = messageProperties.getProperty();
		propertyList.add(mimeType);
		propertyList.add(originalSender);
		propertyList.add(finalRecipient);
		userMessage.setPartyInfo(partyInfo);
		userMessage.setCollaborationInfo(collaborationInfo);
		userMessage.setMessageProperties(messageProperties);
		return userMessage ;
	}
}
