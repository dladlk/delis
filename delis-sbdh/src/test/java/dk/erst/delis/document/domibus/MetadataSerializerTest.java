package dk.erst.delis.document.domibus;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.domibus.plugin.fs.ebms3.UserMessage;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

public class MetadataSerializerTest {

	@Test
	public void testSerialize() throws JAXBException {
		MetadataBuilder b = new MetadataBuilder();
		ParticipantIdentifier pi = ParticipantIdentifier.of("0088:test1");
		ProcessIdentifier pri = ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
		DocumentTypeIdentifier dt = DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1");
		Header header = Header.of(pi, pi, pri, dt);
		UserMessage um = b.buildUserMessage(header, "dynconcepttestparty01gw");
		
		MetadataSerializer ms = new MetadataSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ms.serialize(um, baos);
		
		String result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		System.out.println(result);
		assertNotNull(result);
		assertTrue(result.contains("text/xml"));
	}

}
