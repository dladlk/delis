package dk.erst.delis.web.organisation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierRepository;
import dk.erst.delis.data.IdentifierPublishingStatus;
import dk.erst.delis.data.IdentifierStatus;
import lombok.Getter;

@Service
public class OrganisationStatisticsService {

	@Autowired
	private IdentifierRepository identifierRepository;

	public Map<Long, OrganisationIdentifierStatData> loadOrganisationIdentifierStatMap() {
		List<Map<String, Object>> rawList = this.identifierRepository.loadIndetifierStat();
		return processStatListMap(rawList);
	}

	public OrganisationIdentifierStatData loadOrganisationIdentifierStatMap(long organisationId) {
		List<Map<String, Object>> rawList = this.identifierRepository.loadIndetifierStatByOrganisation(organisationId);
		return processStatListMap(rawList).get(organisationId);
	}

	
	private Map<Long, OrganisationIdentifierStatData> processStatListMap(List<Map<String, Object>> rawList) {
		Map<Long, OrganisationIdentifierStatData> mapRes = new HashMap<>();
		for (Map<String, Object> map : rawList) {
			Long organisationId = (Long) map.get("organisationId");
			OrganisationIdentifierStatData d = mapRes.get(organisationId);
			if (d == null) {
				d = new OrganisationIdentifierStatData();
				mapRes.put(organisationId, d);
			}

			int identifierCount = ((Long) map.get("identifierCount")).intValue();
			IdentifierStatus status = (IdentifierStatus) map.get("status");
			IdentifierPublishingStatus publishingStatus = (IdentifierPublishingStatus) map.get("publishingStatus");
			if (publishingStatus.isFailed()) {
				d.failed += identifierCount;
			}
			if (!(status.isDeleted() && publishingStatus.isDone())) {
				// Done deletions should be excluded from total
				d.total += identifierCount;
			}
			if (status.isActive()) {
				if (publishingStatus.isDone()) {
					d.activeDone += identifierCount;
				} else if (publishingStatus.isPending()) {
					d.activePending += identifierCount;
				}
			} else {
				if (publishingStatus.isPending()) {
					d.disabledPending += identifierCount;
				}
			}
		}

		return mapRes;
	}

	@Getter
	protected static class OrganisationIdentifierStatData {

		private int total;
		private int activeDone;
		private int activePending;
		private int disabledPending;
		private int failed;

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.total);
			sb.append(" = ");
			sb.append(this.activeDone);
			sb.append(" + ");
			sb.append(this.activePending);
			sb.append(" + ");
			sb.append(this.disabledPending);
			sb.append(" + ");
			sb.append(this.failed);
			return sb.toString();
		}
	}
}
