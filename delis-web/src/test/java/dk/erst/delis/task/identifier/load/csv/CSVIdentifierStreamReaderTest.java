package dk.erst.delis.task.identifier.load.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import dk.erst.delis.TestUtil;
import dk.erst.delis.data.entities.identifier.Identifier;

public class CSVIdentifierStreamReaderTest {

	private static final Charset CS = StandardCharsets.ISO_8859_1;

	@Test
	public void testLoadValidCSV() throws IdentifierListParseException {
		InputStream is = TestUtil.getResourceByClass(getClass(), "LF_ISO88591.csv");
		assertNotNull(is);

		CSVIdentifierStreamReader r = new CSVIdentifierStreamReader(is, CS, ';');

		int count = 0;
		while (r.hasNext()) {
			Identifier i = r.next();
			count++;
			assertNotNull(i.getValue());
		}

		assertEquals(10, count);
	}

	@Test
	public void testLoadInvalidCSV() {
		runCase("ean;number\n234234;", "CSV file is not valid: expected column names are not found. Expected columns: 'number;name', parsed: 'ean;number'");
		runCase("name;number\ntest\ntest", "CSV file is not valid: line 1 contains less than 2 elements: 'test'");
		runCase("name;number\ntest;\ntest", "CSV file is not valid: line 1 contains identifier, whose type cannot be resolved: 'test;'");
		runCase("number;name\n00000115;Invalid CVR\ntest", "CSV file is not valid: line 1 contains identifier, which is not valid: '00000115;Invalid CVR'");
		runCase("number;name\n00000116;\ntest", CSVIdentifierStreamReader.END_FORMAT_DESCRIPTION + ", but last line contains not a valid number: test");
		runCase("number;name\n00000116;\n2", CSVIdentifierStreamReader.END_FORMAT_DESCRIPTION + ", but given total number is 2, although passed 1 identifiers");
		runCase("number;name", CSVIdentifierStreamReader.END_FORMAT_DESCRIPTION);
		runCase("number;name\n00000116;Wrong;Wrong", CSVIdentifierStreamReader.END_FORMAT_DESCRIPTION+", but last line other columns are not empty: '00000116;Wrong;Wrong'");

		runCase("number;name\n00000116;OK\n1;;", null);
	}

	private void runCase(String testCase, String expectedError) {
		try {
			new CSVIdentifierStreamReader(buildExample(testCase), CS, ';');
			if (expectedError != null) {
				fail("Expected error: " + expectedError);
			}
		} catch (IdentifierListParseException e) {
			if (expectedError != null) {
				assertEquals(expectedError, e.getMessage());
			} else {
				fail("Unexpected error: "+e.getMessage());
			}
		}
	}

	public InputStream buildExample(String csv) {
		return new ByteArrayInputStream(csv.getBytes(CS));
	}

}
