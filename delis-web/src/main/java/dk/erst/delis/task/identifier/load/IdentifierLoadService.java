package dk.erst.delis.task.identifier.load;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierGroupDaoRepository;
import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.dao.JournalOrganisationDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.dao.SyncOrganisationFactDaoRepository;
import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.IdentifierGroup;
import dk.erst.delis.data.IdentifierPublishingStatus;
import dk.erst.delis.data.IdentifierStatus;
import dk.erst.delis.data.IdentifierValueType;
import dk.erst.delis.data.JournalIdentifier;
import dk.erst.delis.data.JournalOrganisation;
import dk.erst.delis.data.Organisation;
import dk.erst.delis.data.SyncOrganisationFact;
import dk.erst.delis.task.identifier.load.csv.CSVIdentifierStreamReader;

@Service
public class IdentifierLoadService {

	@Autowired
	private IdentifierDaoRepository identifierDaoRepository;

	@Autowired
	private JournalIdentifierDaoRepository journalIdentifierDaoRepository;

	@Autowired
	private IdentifierGroupDaoRepository identifierGroupDaoRepository;

	@Autowired
	private OrganisationDaoRepository organisationDaoRepository;

	@Autowired
	private JournalOrganisationDaoRepository journalOrganisationDaoRepository;

	@Autowired
	private SyncOrganisationFactDaoRepository syncOrganisationFactDaoRepository;

	public SyncOrganisationFact loadCSV(String organisationCode, InputStream inputStream, String description) {
		AbstractIdentifierStreamReader reader = new CSVIdentifierStreamReader(inputStream, StandardCharsets.ISO_8859_1, ';');
		return load(organisationCode, reader, description);
	}

	public SyncOrganisationFact load(String organisationCode, AbstractIdentifierStreamReader reader, String description) {
		Organisation organisation = organisationDaoRepository.findByCode(organisationCode);
		if (organisation == null) {
			throw new RuntimeException("Not found organisation by code " + organisationCode);
		}

		long start = System.currentTimeMillis();

		saveJournalOrganisationMessage(organisation, "Start identifiers synchronization by " + description);

		String identifierGroupCode = IdentifierGroup.DEFAULT_CODE;

		IdentifierGroup identifierGroup = identifierGroupDaoRepository.findByOrganisationAndCode(organisation, identifierGroupCode);
		if (identifierGroup == null) {
			identifierGroup = new IdentifierGroup();
			identifierGroup.setOrganisation(organisation);
			identifierGroup.setCode(identifierGroupCode);
			identifierGroup.setName(identifierGroupCode);

			identifierGroupDaoRepository.save(identifierGroup);

			saveJournalOrganisationMessage(organisation, "Created new identifier group with code " + identifierGroupCode);
		}

		SyncOrganisationFact stat = new SyncOrganisationFact();
		stat.setOrganisation(organisation);
		stat.setDescription(description);

		syncOrganisationFactDaoRepository.save(stat);

		try {
			while (reader.hasNext()) {
				Identifier identifier = reader.next();

				stat.incrementTotal();

				String identifierType = defineIdentifierType(identifier);
				if (identifierType != null) {
					identifier.setType(identifierType);

					if (identifier.getName() != null) {
						if (identifier.getName().length() > 128) {
							identifier.setName(identifier.getName().substring(0, 128));
						}
					} else {
						identifier.setName("");
					}

					Identifier present = identifierDaoRepository.findByValueAndType(identifier.getValue(), identifier.getType());
					if (present == null) {
						stat.incrementAdd();

						identifier.setOrganisation(organisation);
						identifier.setIdentifierGroup(identifierGroup);
						identifier.setPublishingStatus(IdentifierPublishingStatus.PENDING);
						identifier.setStatus(IdentifierStatus.ACTIVE);
						identifier.setLastSyncOrganisationFactId(stat.getId());
						identifier.setUniqueValueType(buildUniqueValueType(identifier));

						saveIdentifier(identifier);

						saveJournalIdentifierMessage(organisation, identifier, "Created by " + description);
					} else {
						if (present.getOrganisation().getId() != organisation.getId()) {
							if (present.getStatus().isActive()) {
								stat.incrementFailed();
								saveJournalIdentifierMessage(organisation, present, "Tried to import into another organisation " + organisation.getName() + " by " + description);
								saveJournalOrganisationMessage(organisation, "Identifier is already registered at " + organisation.getName() + " and is active there: " + identifier.getValue());
							} else {
								stat.incrementAdd();
								
								Organisation previousOrganisation = present.getOrganisation();
								
								present.setOrganisation(organisation);
								present.setIdentifierGroup(identifierGroup);
								present.setStatus(IdentifierStatus.ACTIVE);
								present.setPublishingStatus(IdentifierPublishingStatus.PENDING);
								present.setUniqueValueType(buildUniqueValueType(identifier));
								present.setLastSyncOrganisationFactId(stat.getId());

								saveIdentifier(present);

								saveJournalIdentifierMessage(organisation, identifier, "Moved deactivated from " + previousOrganisation + " by " + description);
							}
						} else {
						
							if (present.getName().equals(identifier.getName()) && present.getIdentifierGroup().getId().equals(identifierGroup.getId())) {
								stat.incrementEqual();
	
								present.setLastSyncOrganisationFactId(stat.getId());
								saveIdentifier(present);
	
							} else {
								present.setName(identifier.getName());
								present.setIdentifierGroup(identifierGroup);
								// TODO: What if identifier group is changed?
								// TODO: What if identifier name is changed - should we republish identifier?
								present.setLastSyncOrganisationFactId(stat.getId());
	
								saveIdentifier(present);
								stat.incrementUpdate();
								saveJournalIdentifierMessage(organisation, present, "Updated");
							}
						}
					}
				} else {
					saveJournalOrganisationMessage(organisation, "Failed to define type of identifier value " + identifier.getValue());
					stat.incrementFailed();
				}
			}

			int deactivated = deactivateAbsent(organisation, stat);
			stat.setDelete(deactivated);

		} finally {
			stat.setDurationMs(System.currentTimeMillis() - start);
			saveJournalOrganisationMessage(organisation, "Finished loading", System.currentTimeMillis() - start);
			syncOrganisationFactDaoRepository.save(stat);
		}

		return stat;
	}

	private String buildUniqueValueType(Identifier identifier) {
		return identifier.getType()+"::"+identifier.getValue();
	}

	private int deactivateAbsent(Organisation organisation, SyncOrganisationFact stat) {
		final int count[] = new int[] { 0 };
		List<Identifier> list = identifierDaoRepository.getPendingForDeactivation(organisation.getId(), stat.getId());
		if (list != null) {
			list.forEach(i -> {
				i.setStatus(IdentifierStatus.DELETED);
				if (i.getPublishingStatus() == IdentifierPublishingStatus.DONE) {
					i.setPublishingStatus(IdentifierPublishingStatus.PENDING);
				}
				identifierDaoRepository.save(i);
				saveJournalIdentifierMessage(organisation, i, "Deleted by " + stat.getDescription());
				count[0]++;
			});
		}
		return count[0];
	}

	private void saveIdentifier(Identifier identifier) {
		identifierDaoRepository.save(identifier);
	}

	private void saveJournalIdentifierMessage(Organisation organisation, Identifier identifier, String journalMessage) {
		JournalIdentifier s = new JournalIdentifier();
		s.setOrganisation(organisation);
		s.setIdentifier(identifier);
		s.setMessage(journalMessage);
		journalIdentifierDaoRepository.save(s);
	}

	private void saveJournalOrganisationMessage(Organisation organisation, String journalMessage) {
		saveJournalOrganisationMessage(organisation, journalMessage, null);
	}

	private void saveJournalOrganisationMessage(Organisation organisation, String journalMessage, Long durationMs) {
		JournalOrganisation s = new JournalOrganisation();
		s.setOrganisation(organisation);
		s.setMessage(journalMessage);
		s.setDurationMs(durationMs);
		journalOrganisationDaoRepository.save(s);
	}

	protected String defineIdentifierType(Identifier identifier) {
		if (identifier == null) {
			return null;
		}
		String value = identifier.getValue();
		if (value != null) {
			if (value.length() == 13) {
				if (value.matches("\\d{13}")) {
					return IdentifierValueType.GLN.getCode();
				}
			}
			if (value.length() == 10 && value.matches("DK\\d{8}")) {
				return IdentifierValueType.DK_CVR.getCode();
			}
			if (value.length() == 8 && value.matches("\\d{8}")) {
				return IdentifierValueType.DK_CVR.getCode();
			}
		}
		return null;
	}

}
