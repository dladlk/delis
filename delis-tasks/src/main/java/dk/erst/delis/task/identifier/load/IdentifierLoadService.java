package dk.erst.delis.task.identifier.load;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.data.enums.identifier.IdentifierValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierGroupDaoRepository;
import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.dao.JournalOrganisationDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.dao.SyncOrganisationFactDaoRepository;
import dk.erst.delis.task.identifier.load.csv.IdentifierListParseException;
import dk.erst.delis.task.identifier.load.csv.CSVIdentifierStreamReader;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;

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
	
	@Autowired
	private OrganisationSetupService organisationSetupService;

	public SyncOrganisationFact loadCSV(String organisationCode, InputStream inputStream, String description) throws IdentifierListParseException {
		Organisation organisation = organisationDaoRepository.findByCode(organisationCode);
		if (organisation == null) {
			throw new RuntimeException("Not found organisation by code " + organisationCode);
		}

		long start = System.currentTimeMillis();
		
		AbstractIdentifierStreamReader reader;
		try {
			reader = new CSVIdentifierStreamReader(inputStream, StandardCharsets.ISO_8859_1, ';');
		} catch (IdentifierListParseException e) {
			String errorMessage = e.getMessage()+". File "+description;
			saveJournalOrganisationMessage(organisation, errorMessage, System.currentTimeMillis() - start);
			throw e;
		}
		
		
		OrganisationSetupData setupData = organisationSetupService.load(organisation);
		boolean schedulePublish = false;
		if (setupData.isSmpIntegrationPublish()) {
			schedulePublish = true;
		}
		IdentifierPublishingStatus publishingStatus = schedulePublish ? IdentifierPublishingStatus.PENDING : null;

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

				IdentifierValueType identifierType = defineIdentifierType(identifier);
				if (identifierType != null) {
					identifier.setType(identifierType.getCode());
					/*
					 * If CVR does not start with DK prefix - add it.
					 */
					if (IdentifierValueType.DK_CVR == identifierType) {
						if (!identifier.getValue().startsWith("DK") && identifier.getValue().length() == 8) {
							identifier.setValue("DK"+identifier.getValue());
						}
					}

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
						identifier.setPublishingStatus(publishingStatus);
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
								saveJournalOrganisationMessage(organisation, "Identifier is already registered at " + present.getOrganisation().getName() + " and is active there: " + identifier.getValue());
							} else {
								stat.incrementAdd();
								
								String previousOrganisationName = present.getOrganisation().getName();
								
								present.setOrganisation(organisation);
								present.setName(identifier.getName());
								present.setIdentifierGroup(identifierGroup);
								present.setStatus(IdentifierStatus.ACTIVE);
								present.setPublishingStatus(publishingStatus);
								present.setUniqueValueType(buildUniqueValueType(identifier));
								present.setLastSyncOrganisationFactId(stat.getId());

								saveIdentifier(present);

								saveJournalIdentifierMessage(organisation, present, "Moved from " + previousOrganisationName + " to " + organisation.getName() + " by " + description);
							}
						} else {
						
							if (present.getName().equals(identifier.getName()) && present.getIdentifierGroup().getId().equals(identifierGroup.getId()) && present.getStatus() == IdentifierStatus.ACTIVE) {
								stat.incrementEqual();
	
								present.setLastSyncOrganisationFactId(stat.getId());
								saveIdentifier(present);
	
							} else {
								present.setName(identifier.getName());
								if (present.getStatus() !=  IdentifierStatus.ACTIVE) {
									present.setStatus(IdentifierStatus.ACTIVE);
									present.setPublishingStatus(publishingStatus);
								}
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

			int deactivated = deactivateAbsent(organisation, stat, schedulePublish);
			stat.setDelete(deactivated);

		} finally {
			stat.setDurationMs(System.currentTimeMillis() - start);
			saveJournalOrganisationMessage(organisation, "Finished loading", System.currentTimeMillis() - start);
			syncOrganisationFactDaoRepository.save(stat);
		}

		return stat;
	}

	public static String buildUniqueValueType(Identifier identifier) {
		return identifier.getType()+"::"+identifier.getValue();
	}

	private int deactivateAbsent(Organisation organisation, SyncOrganisationFact stat, boolean schedulePublish) {
		final int count[] = new int[] { 0 };
		List<Identifier> list = identifierDaoRepository.getPendingForDeactivation(organisation.getId(), stat.getId());
		if (list != null) {
			list.forEach(i -> {
				i.setStatus(IdentifierStatus.DELETED);
				if (schedulePublish) {
					if (i.getPublishingStatus() == IdentifierPublishingStatus.DONE) {
						i.setPublishingStatus(IdentifierPublishingStatus.PENDING);
					}
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

	public static IdentifierValueType defineIdentifierType(Identifier identifier) {
		if (identifier == null) {
			return null;
		}
		String value = identifier.getValue();
		if (value != null) {
			if (value.length() == 13) {
				if (value.matches("\\d{13}")) {
					return IdentifierValueType.GLN;
				}
			}
			if (value.length() == 10 && value.matches("DK\\d{8}")) {
				return IdentifierValueType.DK_CVR;
			}
			if (value.length() == 8 && value.matches("\\d{8}")) {
				return IdentifierValueType.DK_CVR;
			}
		}
		return null;
	}

}
