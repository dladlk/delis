package dk.erst.delis.domibus.sender.service;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.*;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

import java.util.List;

import dk.erst.delis.document.domibus.MetadataBuilder;

public class WSStubUserMessageBuilder {

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
        service.setType(MetadataBuilder.DEFAULT_PROCESS_SCHEME_ID);
        service.setValue(sbdhHeader.getProcess().getIdentifier());
        CollaborationInfo collaborationInfo = new CollaborationInfo();
        collaborationInfo.setAgreementRef(agreementRef);
        collaborationInfo.setService(service);
        collaborationInfo.setAction(sbdhHeader.getDocumentType().toString());
        ParticipantIdentifier sender = sbdhHeader.getSender();
        ParticipantIdentifier receiver = sbdhHeader.getReceiver();
        Property originalSender = createProperty("originalSender", sender.getIdentifier(), sender.getScheme().getIdentifier());
        Property finalRecipient = createProperty("finalRecipient", receiver.getIdentifier(), receiver.getScheme().getIdentifier());
        MessageProperties messageProperties = new MessageProperties();
        List<Property> propertyList = messageProperties.getProperty();
        propertyList.add(originalSender);
        propertyList.add(finalRecipient);
        userMessage.setPartyInfo(partyInfo);
        userMessage.setCollaborationInfo(collaborationInfo);
        userMessage.setMessageProperties(messageProperties);


        PayloadInfo payloadInfo = new PayloadInfo();
        PartInfo partInfo = new PartInfo();
        partInfo.setHref("cid:message");
        PartProperties partProperties = new PartProperties();
        partProperties.getProperty().add(createProperty("text/xml", "MimeType", "string"));
        partInfo.setPartProperties(partProperties);

        payloadInfo.getPartInfo().add(partInfo);
        userMessage.setPayloadInfo(payloadInfo);


        return userMessage;
    }

    protected Property createProperty(String name, String value, String type) {
        Property aProperty = new Property();
        aProperty.setValue(value);
        aProperty.setName(name);
        aProperty.setType(type);
        return aProperty;
    }
}
