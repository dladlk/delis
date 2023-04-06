package dk.erst.delis.document.domibus;

import eu.domibus.plugin.fs.ebms3.UserMessage;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MetadataSerializerTest {

	@Test
	public void testSerialize() throws JAXBException {
		MetadataBuilder metadataBuilder = new MetadataBuilder();
		ParticipantIdentifier sender = ParticipantIdentifier.of("0088:test1s");
		ParticipantIdentifier receiver = ParticipantIdentifier.of("0088:test1r");
		ProcessIdentifier processIdentifier = ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
		DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1");
		Header header = Header.of(sender, receiver, processIdentifier, documentTypeIdentifier);
		UserMessage userMessage = metadataBuilder.buildUserMessage(header, "dynconcepttestparty01gw");
		
		MetadataSerializer metadataSerializer = new MetadataSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		metadataSerializer.serialize(userMessage, baos);
		
		String result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		System.out.println(result);
		assertNotNull(result);
		assertTrue(result.contains("text/xml"));
	}

}
