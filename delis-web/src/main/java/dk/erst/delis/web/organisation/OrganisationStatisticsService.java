package dk.erst.delis.web.organisation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierDaoRepository;

import lombok.Getter;

@Service
public class OrganisationStatisticsService {

	@Autowired
	private IdentifierDaoRepository identifierDaoRepository;

	public Map<Long, OrganisationIdentifierStatData> loadOrganisationIdentifierStatMap() {
		List<Map<String, Object>> rawList = this.identifierDaoRepository.loadIndetifierStat();
		return processStatListMap(rawList);
	}

	public OrganisationIdentifierStatData loadOrganisationIdentifierStatMap(long organisationId) {
		List<Map<String, Object>> rawList = this.identifierDaoRepository.loadIndetifierStatByOrganisation(organisationId);
		OrganisationIdentifierStatData statData = processStatListMap(rawList).get(organisationId);
		if (statData == null) {
			return new OrganisationIdentifierStatData();
		}
		return statData;
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
			
			if (status != null) {
				if (publishingStatus == null) {
					if (status.isActive()) {
						d.total += identifierCount;
					}
				} else {
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
		
		public boolean isNoPublish() {
			return (this.activeDone + this.activePending + this.disabledPending + this.failed) == 0;
		}

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
