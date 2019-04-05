package dk.erst.delis.task.document.deliver;

import com.google.common.io.Files;
import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.*;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.vfs.service.VFSService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DocumentDeliveryServiceTestIT {

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
	private ConfigValueDaoRepository configRepository;

	@Autowired
	DocumentBytesDaoRepository documentBytesDaoRepository;

	@Autowired
	private JournalDocumentService journalDocumentService;
	@Autowired
	private OrganisationSetupService setupService;

	@Autowired
	private VFSService vfsService;

	private Organisation org;
	private File rootFolder;
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
		ConfigBean configBean = new ConfigBean(configRepository) {
			@Override
			public Path getStorageLoadedPath() {
				return rootFolder.toPath();
			}
		};
		DocumentBytesStorageService storageService = new DocumentBytesStorageService(configBean, documentBytesDaoRepository);
		DocumentDeliverService documentDeliverService = new DocumentDeliverService(documentRepository, setupService, journalDocumentService, storageService, vfsService);
		return documentDeliverService;
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

		rootFolder = Files.createTempDir();


		Document doc = new Document();
		doc.setOrganisation(org);
		doc.setDocumentStatus(DocumentStatus.VALIDATE_OK);
		doc.setName("test");
		doc.setReceiverIdentifier(id);
		doc.setMessageId("message_id");
		documentRepository.save(doc);

		DocumentBytes documentBytes = new DocumentBytes();
		documentBytes.setDocument(doc);
		documentBytes.setType(DocumentBytesType.READY);
		documentBytesDaoRepository.save(documentBytes);

		testFile = createTestFile(documentBytes, rootFolder);
		documentBytes.setSize(testFile.toFile().length());
		documentBytesDaoRepository.save(documentBytes);

	}

	private Path createTestFile(DocumentBytes documentBytes, File parentFolder) throws IOException {
		Document document = documentBytes.getDocument();
		Organisation organisation = document.getOrganisation();

		String fileName ="00" + documentBytes.getId() + "-READY.xml"; // there will be less than 10 in this test for sure (in observable future)
		String subfolderName = organisation.getCode() + "/0000" + document.getId();
		Path filePath = Paths.get(parentFolder.toString(), subfolderName);
		File testFile = new File(filePath.toAbsolutePath().toString(), fileName);
		filePath.toFile().mkdirs();
		testFile.createNewFile();
		Files.write("<xml></xml>".getBytes(), testFile);
		return filePath;
	}

	@After
	public void finalizeTest () {
		journalDocumentDaoRepository.deleteAll();
		documentBytesDaoRepository.deleteAll();
		documentRepository.deleteAll();
		identifierRepository.deleteAll();
		identifierGroupRepository.deleteAll();
		organisationSetupRepository.deleteAll();
		organisationRepository.deleteAll();

//		TestDocumentUtil.cleanupTestFile(testFile);
	}
}
