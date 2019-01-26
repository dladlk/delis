package dk.erst.delis.document.domibus;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import eu.domibus.plugin.fs.ebms3.UserMessage;

public class MetadataSerializer {

    private static void writeXML(OutputStream outputStream, Class<UserMessage> clazz, UserMessage objectToWrite) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        QName qName = new QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", clazz.getSimpleName());

        marshaller.marshal(new JAXBElement<UserMessage>(qName, clazz, null, objectToWrite), outputStream);
    }


	public void serialize(UserMessage userMessage, OutputStream out) throws JAXBException {
		writeXML(out, UserMessage.class, userMessage);
	}
}
