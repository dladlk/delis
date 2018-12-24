package dk.erst.delis.task.codelist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;
import dk.erst.delis.config.codelist.DocumentTypeIdentifier;
import dk.erst.delis.config.codelist.ParticipantIdentifierScheme;
import dk.erst.delis.config.codelist.ProcessScheme;
import dk.erst.delis.config.codelist.TransportProfile;

public class CodeListReaderServiceTest {

	@Test
	public void testReadCodeLists() throws Exception {
		ConfigProperties props = new ConfigProperties();
		props.setStorageCodeListsRoot("../delis-resources/codelists");
		ConfigBean configBean = new ConfigBean(props);
		CodeListReaderService c = new CodeListReaderService(configBean);
		
		List<ParticipantIdentifierScheme> identifierCodeList = c.readParticipantIdentifierSchemeList();
		assertFalse(identifierCodeList.isEmpty());
		for (ParticipantIdentifierScheme pis : identifierCodeList) {
			assertNotEmpty(pis.getIssuingOrganization());
			assertNotEmpty(pis.getSchemeID());
			assertNotEmpty(pis.getIcdValue());
		}
		
		List<DocumentTypeIdentifier> documentTypeIdentifierList = c.readDocumentTypeIdentifierList();
		assertFalse(documentTypeIdentifierList.isEmpty());
		for (DocumentTypeIdentifier pis : documentTypeIdentifierList) {
			assertNotEmpty(pis.getProfileCode());
			assertNotEmpty(pis.getDocumentTypeIdentifierScheme());
			assertNotEmpty(pis.getDocumentTypeIdentifier());
		}
		
		List<ProcessScheme> processSchemeList = c.readProcessSchemeList();
		assertFalse(processSchemeList.isEmpty());
		for (ProcessScheme pis : processSchemeList) {
			assertNotEmpty(pis.getProfileCode());
			assertNotEmpty(pis.getProfileID());
			assertNotEmpty(pis.getProcessScheme());
		}

		List<TransportProfile> transportProfileList = c.readTransportProfileList();
		assertFalse(transportProfileList.isEmpty());
		for (TransportProfile pis : transportProfileList) {
			assertNotEmpty(pis.getProfileID());
			assertNotEmpty(pis.getProtocolName());
		}

	}

	private void assertNotEmpty(String v) {
		assertNotNull(v);
		assertTrue(!v.isEmpty());
	}

}
