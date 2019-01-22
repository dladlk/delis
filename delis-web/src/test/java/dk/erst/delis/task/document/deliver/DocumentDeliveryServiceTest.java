package dk.erst.delis.task.document.deliver;

import com.google.common.io.Files;
import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.*;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.TestDocument;
import dk.erst.delis.task.document.TestDocumentUtil;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Path;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DocumentDeliveryServiceTest {

	@Autowired
	private DocumentDaoRepository documentRepository;
	@Autowired
	private OrganisationDaoRepository organisationRepository;
	@Autowired
	private IdentifierGroupDaoRepository identifierGroupRepository;
	@Autowired
	private IdentifierDaoRepository identifierRepository;
	@Autowired
	private OrganisationSetupDaoRepository organisationSetupRepository;
	@Autowired
	private JournalDocumentDaoRepository journalDocumentDaoRepository;

	@Autowired
	private JournalDocumentService journalDocumentService;
	@Autowired
	private OrganisationSetupService setupService;

	private Organisation org;
	private Path testFile;

	@Test
	public void testFileSystem() throws Exception {
		setReceiveMethod(OrganisationReceivingMethod.FILE_SYSTEM.getCode());

		DocumentDeliverService documentDeliverService = getDocumentDeliverService();
		StatData statData = documentDeliverService.processValidated();

		assert (statData.toStatString().contains("OK: 1"));
	}

	@Test
	public void testAzure() throws Exception {
		setReceiveMethod(OrganisationReceivingMethod.AZURE_STORAGE_ACCOUNT.getCode());

		DocumentDeliverService documentDeliverService = getDocumentDeliverService();
		StatData statData = documentDeliverService.processValidated();
		assert (statData.toStatString().contains("ERROR: 1"));
	}

	@Test
	public void testNotConfigured() throws Exception {
		organisationSetupRepository.deleteAll();

		DocumentDeliverService documentDeliverService = getDocumentDeliverService();
		StatData statData = documentDeliverService.processValidated();
		assert (statData.toStatString().contains("no recieving method"));
	}

	private DocumentDeliverService getDocumentDeliverService() {
		DocumentBytesStorageService storageService = new DocumentBytesStorageService(new ConfigBean(null) {
			@Override
			public Path getStorageValidPath() {
				return testFile.getParent();
			}
		});
		return new DocumentDeliverService(documentRepository, setupService, journalDocumentService, storageService);
	}

	private void setReceiveMethod(String method) {
		OrganisationSetup setup = new OrganisationSetup();
		setup.setOrganisation(org);
		setup.setKey(OrganisationSetupKey.RECEIVING_METHOD);
		setup.setValue(method);
		organisationSetupRepository.save(setup);
	}

	@Before
	public void initTest() throws Exception {
		org = new Organisation();
		org.setCode("code1");
		org.setName("org1");
		organisationRepository.save(org);

		IdentifierGroup group = new IdentifierGroup();
		group.setCode("code1");
		group.setName("code1");
		group.setOrganisation(org);
		identifierGroupRepository.save(group);

		Identifier id = new Identifier();
		id.setOrganisation(org);
		id.setName("id1");
		id.setStatus(IdentifierStatus.ACTIVE);
		id.setType("test_type");
		id.setUniqueValueType("test_uniq_type");
		id.setValue("test_value");
		id.setIdentifierGroup(group);
		identifierRepository.save(id);

		OrganisationSetup setup = new OrganisationSetup();
		setup.setOrganisation(org);
		setup.setKey(OrganisationSetupKey.RECEIVING_METHOD_SETUP);
		setup.setValue(Files.createTempDir().getAbsolutePath());
		organisationSetupRepository.save(setup);

		testFile = TestDocumentUtil.createTestFile(TestDocument.CII);

		Document doc = new Document();
		doc.setOrganisation(org);
		doc.setDocumentStatus(DocumentStatus.VALIDATE_OK);
		doc.setOutgoingRelativePath(testFile.toString());
		doc.setIngoingRelativePath(testFile.toString());
		doc.setReceiverIdentifier(id);
		doc.setMessageId("message_id");
		documentRepository.save(doc);


	}

	@After
	public void finalizeTest () {
		journalDocumentDaoRepository.deleteAll();
		documentRepository.deleteAll();
		identifierRepository.deleteAll();
		identifierGroupRepository.deleteAll();
		organisationSetupRepository.deleteAll();
		organisationRepository.deleteAll();

//		TestDocumentUtil.cleanupTestFile(testFile);
	}
}
