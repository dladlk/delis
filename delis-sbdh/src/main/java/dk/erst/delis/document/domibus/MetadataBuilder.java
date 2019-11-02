package dk.erst.delis.document.domibus;

import java.util.List;

import eu.domibus.plugin.fs.ebms3.AgreementRef;
import eu.domibus.plugin.fs.ebms3.CollaborationInfo;
import eu.domibus.plugin.fs.ebms3.From;
import eu.domibus.plugin.fs.ebms3.MessageProperties;
import eu.domibus.plugin.fs.ebms3.PartInfo;
import eu.domibus.plugin.fs.ebms3.PartProperties;
import eu.domibus.plugin.fs.ebms3.PartyId;
import eu.domibus.plugin.fs.ebms3.PartyInfo;
import eu.domibus.plugin.fs.ebms3.PayloadInfo;
import eu.domibus.plugin.fs.ebms3.Property;
import eu.domibus.plugin.fs.ebms3.Service;
import eu.domibus.plugin.fs.ebms3.UserMessage;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

public class MetadataBuilder {

	public static String DEFAULT_PROCESS_SCHEME_ID = "urn:fdc:peppol.eu:2017:identifiers:proc-id";
	
	private static final boolean PASS_MIME_TYPE_AS_PAYLOAD_INFO = true;

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

		MessageProperties messageProperties = new MessageProperties();
		List<Property> propertyList = messageProperties.getProperty();

		if (PASS_MIME_TYPE_AS_PAYLOAD_INFO) {
			PayloadInfo payloadInfo = buildPayloadInfo("cid:message", "MimeType", "text/xml");
			userMessage.setPayloadInfo(payloadInfo);
		} else {
			Property mimeType = new Property();
			mimeType.setName("mimeType");
			mimeType.setValue("text/xml");
			propertyList.add(mimeType);
		}

		propertyList.add(originalSender);
		propertyList.add(finalRecipient);
		userMessage.setPartyInfo(partyInfo);
		userMessage.setCollaborationInfo(collaborationInfo);
		userMessage.setMessageProperties(messageProperties);
		return userMessage ;
	}

	private PayloadInfo buildPayloadInfo(String href, String propertyName, String propertyValue) {
		PayloadInfo payloadInfo = new PayloadInfo();
		PartInfo partInfo = new PartInfo();
		partInfo.setHref(href);
		PartProperties partProperties = new PartProperties();
		Property p = new Property();
		p.setName(propertyName);
		p.setValue(propertyValue);
		partProperties.getProperty().add(p);
		partInfo.setPartProperties(partProperties);
		payloadInfo.getPartInfo().add(partInfo);
		return payloadInfo;
	}
}
