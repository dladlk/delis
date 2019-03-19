package dk.erst.delis.task.document.process;

import com.google.common.io.Files;
import dk.erst.delis.common.util.StatData;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class DocumentProcessServiceTest {

	@Autowired
	private DocumentProcessService documentProcessService;
    @Autowired
    private DocumentDaoRepository documentDaoRepository;
    @Autowired
    private OrganisationDaoRepository organisationRepository;
    @Autowired
    private IdentifierGroupDaoRepository identifierGroupRepository;
    @Autowired
    private IdentifierDaoRepository identifierRepository;
    @Autowired
    private OrganisationSetupDaoRepository organisationSetupRepository;
    @Autowired
    private DocumentBytesDaoRepository documentBytesDaoRepository;

    @Test
    public void testProcessLoaded() {
        StatData statData = documentProcessService.processLoaded();
        System.out.println(statData.toStatString());
        assertNotNull(statData);
        Document document = prepareTestDoc();
        statData = new StatData();
        documentProcessService.processDocument(statData, document);
        assertEquals("Document xml not found: 1", statData.toStatString());
    }

    public Document prepareTestDoc() {
        Organisation org = new Organisation();
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

        Document doc = new Document();
        doc.setOrganisation(org);
        doc.setDocumentStatus(DocumentStatus.VALIDATE_OK);
        doc.setName("test");
        doc.setReceiverIdentifier(id);
        doc.setMessageId("message_id");
        documentDaoRepository.save(doc);

        DocumentBytes documentBytes = new DocumentBytes();
        documentBytes.setDocument(doc);
        documentBytes.setType(DocumentBytesType.READY);
        documentBytesDaoRepository.save(documentBytes);

        documentBytes.setSize(1024L);
        documentBytesDaoRepository.save(documentBytes);

        return doc;
    }

}
