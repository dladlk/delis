package dk.erst.delis.task.identifier.load.csv;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import dk.erst.delis.data.Identifier;

class CSVIdentifierStreamReaderTest {

	@Test
	void testLoadCSV() {
		InputStream is = getResourceByClass(getClass(), "LF_ISO88591.csv");
		assertNotNull(is);

		CSVIdentifierStreamReader r = new CSVIdentifierStreamReader(is, StandardCharsets.ISO_8859_1, ';');
		r.start();
		
		int count = 0;
		while (r.hasNext()) {
			Identifier i = r.next();
			count++;
			
			assertNotNull(i.getValue());
		}
		
		assertEquals(10, count);
	}
	
	private InputStream getResourceByClass(Class<?> cls, String suffix) {
		return cls.getResourceAsStream(cls.getSimpleName()+"_"+suffix);
	}

}
