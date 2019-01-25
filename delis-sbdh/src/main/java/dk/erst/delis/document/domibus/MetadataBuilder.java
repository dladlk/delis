package dk.erst.delis.document.domibus;

import eu.domibus.plugin.fs.ebms3.MessageProperties;
import eu.domibus.plugin.fs.ebms3.Property;
import eu.domibus.plugin.fs.ebms3.UserMessage;
import no.difi.vefa.peppol.common.model.Header;

public class MetadataBuilder {

	public UserMessage buildUserMessage(Header sbdhHeader) {
		UserMessage um = new UserMessage();
		MessageProperties messageProperties = new MessageProperties();
		Property mimeType = new Property();
		mimeType.setName("mimeType");
		mimeType.setValue("text/xml");
		messageProperties.getProperty().add(mimeType);
		um.setMessageProperties(messageProperties);
		return um ;
	}
}
