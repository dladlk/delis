package dk.erst.delis.task.document.load;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import dk.erst.delis.data.Document;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.parse.DocumentParseService;

public class DocumentLoadServiceTest {

	@Test
	public void testLoadFile() throws IOException {
		DocumentLoadService dls = new DocumentLoadService(null, new DocumentParseService(), null);
		Document document = dls.loadFile(Paths.get(TestDocument.OIOUBL_CREDITNOTE.getFilePath()));
		assertNotNull(document);
	}

}
