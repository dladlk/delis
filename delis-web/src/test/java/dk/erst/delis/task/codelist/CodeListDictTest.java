package dk.erst.delis.task.codelist;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.ConfigProperties;

public class CodeListDictTest {

	@Test
	public void testFindParticipantIdentifierValues() {
		CodeListReaderService clReaderService = new CodeListReaderService(new ConfigBean(new ConfigProperties()));
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
