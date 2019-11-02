package dk.erst.delis.web.error;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class ErrorDictionaryServiceTest {

	@Autowired
	private DocumentDaoRepository documentDaoRepository;
	@Autowired
	private JournalDocumentDaoRepository journalDocumentDaoRepository;

	@Autowired
	private ErrorDictionaryDaoRepository errorDictionaryDaoRepository;
	@Autowired
	private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;
	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Test
	public void testReorg() throws IOException {
		prepareTestData();

		String expectedTotal;
		try (InputStreamReader isr = getStream("_expected.txt")) {
			expectedTotal = IOUtils.readLines(isr).stream().collect(Collectors.joining("\r\n"));
		}

		ErrorDictionaryService errorDictionaryService = new ErrorDictionaryService(errorDictionaryDaoRepository, journalDocumentErrorDaoRepository);

		StatData stat = errorDictionaryService.reorg();

		Query sql = entityManager.createNativeQuery("select code, error_type, flag, hash, location, message from error_dictionary order by code, error_type, flag, hash, location, message");
		List<Object[]> resultList = sql.getResultList();

		StringBuilder actualTotal = new StringBuilder();
		for (Object[] objects : resultList) {
			if (actualTotal.length() > 0) {
				actualTotal.append("\r\n");
			}
			String line = Arrays.stream(objects).map(s -> String.valueOf(s)).collect(Collectors.joining("\t"));
			actualTotal.append(line);
		}

		assertNotNull(stat);

		assertEquals(expectedTotal, actualTotal.toString());
		assertEquals("DELETED: 14, JOURNAL_UPDATED: 14, LOADED: 35, REHASHED: 20, UPDATED: 15", stat.toStatString());
	}

	private void prepareTestData() {
		// select code, error_type, flag, hash, location, message from error_dictionary order by id_pk asc limit 10;
		try (BufferedReader reader = new BufferedReader(getStream("_input.txt"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					String parts[] = line.split("\t");
					ErrorDictionary e = new ErrorDictionary();
					e.setCode(parts[0]);
					e.setErrorType(DocumentErrorCode.valueOf(parts[1]));
					e.setFlag(parts[2]);
					e.setHash(Integer.valueOf(parts[3]));
					e.setLocation(parts[4]);
					e.setMessage(parts[5]);

					this.errorDictionaryDaoRepository.save(e);

					Document doc = new Document();
					doc.setDocumentStatus(DocumentStatus.LOAD_ERROR);
					doc.setName("test");
					doc.setMessageId("test");

					this.documentDaoRepository.save(doc);

					JournalDocument jd = new JournalDocument();
					jd.setDocument(doc);
					jd.setSuccess(false);
					jd.setType(DocumentProcessStepType.LOAD);
					jd.setMessage("test");

					this.journalDocumentDaoRepository.save(jd);

					JournalDocumentError jde = new JournalDocumentError();
					jde.setErrorDictionary(e);
					jde.setJournalDocument(jd);
					this.journalDocumentErrorDaoRepository.save(jde);
				}
			}
			;
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		;
	}

	private InputStreamReader getStream(String suffix) {
		String resourceName = this.getClass().getSimpleName() + suffix;
		return new InputStreamReader(this.getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
	}
}
