package dk.erst.delis.task.codelist;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.codelist.DocumentTypeIdentifier;
import dk.erst.delis.config.codelist.ParticipantIdentifierScheme;
import dk.erst.delis.config.codelist.ProcessScheme;
import dk.erst.delis.config.codelist.TransportProfile;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CodeListReaderServiceTest {

	@Autowired
	private ConfigValueDaoRepository configRepository;

	@Test
	public void testReadCodeLists() throws Exception {
		ConfigBean configBean = new ConfigBean(configRepository);
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
