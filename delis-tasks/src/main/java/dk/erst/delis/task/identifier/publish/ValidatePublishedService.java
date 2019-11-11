package dk.erst.delis.task.identifier.publish;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierValueType;
import dk.erst.delis.task.identifier.publish.ValidatePublishedTreeBuilder.TreeNode;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.web.identifier.IdentifierService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

@Service
public class ValidatePublishedService {

	private IdentifierPublishDataService identifierPublishDataService;

	private IdentifierService identifierService;

	private OrganisationSetupService organisationSetupService;

	private SmpLookupService smpLookupService;

	@Autowired
	public ValidatePublishedService(ConfigBean configBean, IdentifierPublishDataService identifierPublishDataService, IdentifierService identifierService, OrganisationSetupService organisationSetupService) {
		this.smpLookupService = new SmpLookupService(configBean);
		this.identifierPublishDataService = identifierPublishDataService;
		this.identifierService = identifierService;
		this.organisationSetupService = organisationSetupService;
	}

	@Getter
	@Setter
	@ToString
	public static class ValidatePublishedResult {
		private SmpPublishData expected;
		
		private int total;

		private int processed;
		private int processedDeleted;
		public int getProcessedActive() {
			return this.total - this.processedDeleted;
		}
		
		private int matched;
		public long duration;
		
		public int getNotMatched() {
			return this.processed - this.matched;
		}
		
		private List<IdentifierResult> identifierResultList = new ArrayList<ValidatePublishedService.IdentifierResult>();

		public void add(IdentifierResult identifierResult) {
			this.identifierResultList.add(identifierResult);
		}
	}

	@Getter
	@Setter
	@ToString
	public static class IdentifierResult {
		private Identifier identifier;
		private ParticipantIdentifier participantIdentifier;
		private SmpPublishData actualPublished;
		
		private TreeNode actualPublishedTree;
		
		private long duration;

		private boolean published;
		private boolean matchAllProfiles;
		private boolean supportsMoreProfiles;
		private boolean matchAccessPointAS4;
		private boolean matchAccessPointAS2;
		private boolean supportsMoreAccessPoints;

		private boolean matchSuccess;
	}

	public void validatePublishedIdentifiers(Organisation organisation, ValidatePublishedResult resultList) {
		long startTotal = System.currentTimeMillis();
		
		OrganisationSetupData setupData = organisationSetupService.load(organisation);
		Identifier templateIdentifier = new Identifier();
		templateIdentifier.setType(IdentifierValueType.GLN.getCode());
		templateIdentifier.setValue("template");
		SmpPublishData template = identifierPublishDataService.buildPublishData(templateIdentifier, setupData);

		resultList.setExpected(template);
		
		resultList.total = identifierService.countByOrganisation(organisation);

		boolean moreIdentifiers = false;
		long previousId = 0;
		do {
			List<Identifier> list = identifierService.loadIdentifierList(organisation, previousId, PageRequest.of(0, 10));
			moreIdentifiers = !list.isEmpty();
			for (Identifier identifier : list) {
				SmpPublishData expected = null;
				if (!identifier.getStatus().isDeleted()) {
					expected = template;
				}
				ParticipantIdentifier participantIdentifier = identifierPublishDataService.buildParticipantIdentifier(identifier);

				long start = System.currentTimeMillis();
				SmpPublishData actual = smpLookupService.lookup(participantIdentifier, true);
				if (actual == null) {
					smpLookupService.lookup(participantIdentifier, false);
				}

				IdentifierResult result = compareResult(expected, actual);
				result.setActualPublished(actual);
				result.setIdentifier(identifier);
				result.setParticipantIdentifier(participantIdentifier);
				result.setDuration(System.currentTimeMillis() - start);
				resultList.add(result);

				previousId = identifier.getId();
				
				if (identifier.getStatus().isDeleted()) {
					resultList.processedDeleted++;
				}
				if (result.isMatchSuccess()) {
					resultList.matched++;
				}
				resultList.processed++;
			}
		} while (moreIdentifiers);
		
		resultList.duration = System.currentTimeMillis() - startTotal;
	}

	private IdentifierResult compareResult(SmpPublishData expected, SmpPublishData actual) {
		IdentifierResult result = new IdentifierResult();

		if (actual != null) {
			result.setPublished(true);

			boolean matchSuccess = true;

			List<SmpPublishServiceData> expServiceList = expected.getServiceList();
			List<SmpPublishServiceData> actServiceList = actual.getServiceList();

			for (SmpPublishServiceData expServiceData : expServiceList) {
				boolean expServiceMatch = false;
				for (SmpPublishServiceData actServiceData : actServiceList) {
					if (expServiceData.isMatch(actServiceData)) {
						expServiceMatch = true;
						break;
					}
				}
				if (!expServiceMatch) {
					matchSuccess = false;
					break;
				}
			}

			result.setMatchSuccess(matchSuccess);
		} else {
			if (expected == null) {
				result.setMatchSuccess(true);
			}
		}
		return result;
	}
}
