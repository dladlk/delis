package dk.erst.delis.document.domibus;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import eu.domibus.plugin.fs.ebms3.UserMessage;
import no.difi.vefa.peppol.common.model.Header;

public class MetadataSerializerTest {

	@Test
	public void testSerialize() throws JAXBException {
		MetadataBuilder b = new MetadataBuilder();
		Header header = Header.of(null, null, null, null);
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
