package dk.erst.delis.task.codelist;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CodeListDictTest {

	@Autowired
	private ConfigValueDaoRepository configRepository;

	@Test
	public void testFindParticipantIdentifierValues() {
		CodeListReaderService clReaderService = new CodeListReaderService(new ConfigBean(configRepository));
		CodeListDict s = new CodeListDict(clReaderService);
		
		assertNull(s.getIdentifierTypeSchemeID("blablabla"));
		assertNull(s.getIdentifierTypeSchemeID(null));
		
		assertEquals("GLN", s.getIdentifierTypeSchemeID("0088"));
		assertEquals("0088", s.getIdentifierTypeIcdValue("GLN"));
		assertEquals("0088", s.getIdentifierTypeIcdValue("gln"));
		
		assertEquals("DK:CVR", s.getIdentifierTypeSchemeID("0184"));
		assertEquals("0184", s.getIdentifierTypeIcdValue("DK:CVR"));
		assertEquals("0184", s.getIdentifierTypeIcdValue("dk:cvr"));
	}

}
