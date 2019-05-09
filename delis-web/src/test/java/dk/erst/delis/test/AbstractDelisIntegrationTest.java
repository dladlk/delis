package dk.erst.delis.test;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.io.Files;

import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.IdentifierGroupDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.dao.OrganisationSetupDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.vfs.service.VFSService;

public abstract class AbstractDelisIntegrationTest {

	protected static Organisation organisation;
	protected static Identifier identifier;
	
	private static boolean initialized = false;

	@Autowired
	protected DocumentDaoRepository documentDaoRepository;
	@Autowired
	protected OrganisationDaoRepository organisationRepository;
	@Autowired
	protected IdentifierGroupDaoRepository identifierGroupRepository;
	@Autowired
	protected IdentifierDaoRepository identifierRepository;
	@Autowired
	protected OrganisationSetupDaoRepository organisationSetupRepository;
	@Autowired
	protected ConfigValueDaoRepository configRepository;
	@Autowired
	protected DocumentBytesDaoRepository documentBytesDaoRepository;
	@Autowired
	protected JournalDocumentService journalDocumentService;
	@Autowired
	protected OrganisationSetupService setupService;
	@Autowired
	protected VFSService vfsService;
	@Autowired
	protected DocumentProcessService documentProcessService;


	/**
	 * @return true if new entities were actually created, otherwise false
	 */
	public boolean init() throws Exception {
		if (AbstractDelisIntegrationTest.initialized) {
			return false;
		}
		organisation = new Organisation();
		organisation.setCode("test1");
		organisation.setName("Test Company 1");
		organisationRepository.save(organisation);

		IdentifierGroup group = new IdentifierGroup();
		group.setCode("default");
		group.setName("default");
		group.setOrganisation(organisation);
		identifierGroupRepository.save(group);

		identifier = new Identifier();
		identifier.setOrganisation(organisation);
		identifier.setName("id1");
		identifier.setStatus(IdentifierStatus.ACTIVE);
		identifier.setType("GLN");
		identifier.setValue("5790201905093");
		identifier.setUniqueValueType(IdentifierLoadService.buildUniqueValueType(identifier));
		identifier.setIdentifierGroup(group);
		identifierRepository.save(identifier);

		OrganisationSetup setup = new OrganisationSetup();
		setup.setOrganisation(organisation);
		setup.setKey(OrganisationSetupKey.RECEIVING_METHOD_SETUP);
		setup.setValue(Files.createTempDir().getAbsolutePath());
		organisationSetupRepository.save(setup);
		
		AbstractDelisIntegrationTest.initialized = true;
		
		return true;
	}
}
