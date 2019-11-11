package dk.erst.delis.task.identifier.publish;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.access.AccessPointType;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpProcessIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.web.accesspoint.AccessPointService;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

@Service
@Slf4j
public class IdentifierPublishDataService {

	private CodeListDict codeListDict;

	private AccessPointService accessPointService;

	private static CertificateFactory certificateFactory;

	private static Map<AccessPointType, String> transportProfilesMap = new HashMap<AccessPointType, String>(){
		private static final long serialVersionUID = -6392733384544701957L;
	{
		put(AccessPointType.AS4, "peppol-transport-as4-v2_0");
		put(AccessPointType.AS2, "busdox-transport-as2-ver1p0");
	}};

	static {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Autowired
	public IdentifierPublishDataService(CodeListDict codeListDict, AccessPointService accessPointService) {
		this.codeListDict = codeListDict;
		this.accessPointService = accessPointService;
	}

	public SmpPublishData buildPublishData(Identifier identifier, OrganisationSetupData organisationSetupData) {
		SmpPublishData publishData = new SmpPublishData();
		ParticipantIdentifier participantIdentifier = buildParticipantIdentifier(identifier);
		List<SmpPublishServiceData> serviceList = createServiceList(identifier, organisationSetupData);
		publishData.setParticipantIdentifier(participantIdentifier);
		publishData.setServiceList(serviceList);
		return publishData;
	}

	public ParticipantIdentifier buildParticipantIdentifier(Identifier identifier) {
		String icdValue = codeListDict.getIdentifierTypeIcdValue(identifier.getType());
		if (icdValue == null) {
			throw new RuntimeException("Identifier type " + identifier.getType() + " is unknown in ICD code lists for identifier " + identifier);
		}
		ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of(icdValue + ":" + identifier.getValue());
		return participantIdentifier;
	}

	private List<SmpPublishServiceData> createServiceList(Identifier identifier, OrganisationSetupData organisationSetupData) {
		List<SmpPublishServiceData> result = new ArrayList<>();
		List<SmpServiceEndpointData> endpointList = createEndpointList(organisationSetupData);
		Set<OrganisationSubscriptionProfileGroup> subscribedProfiles = organisationSetupData.getSubscribeProfileSet();
		for (OrganisationSubscriptionProfileGroup subscribedProfile : subscribedProfiles) {
			List<SmpPublishServiceData> serviceDataList = createServiceData(endpointList, subscribedProfile);
			result.addAll(serviceDataList);
		}
		return result;
	}

	private List<SmpPublishServiceData> createServiceData(List<SmpServiceEndpointData> endpointList, OrganisationSubscriptionProfileGroup subscribedProfile) {
		List<SmpPublishServiceData> result = new ArrayList<>();
		for (String documentIdentifier : subscribedProfile.getDocumentIdentifiers()) {
            SmpPublishServiceData serviceData = new SmpPublishServiceData();
            serviceData.setDocumentIdentifier(SmpDocumentIdentifier.of(documentIdentifier));
            SmpProcessIdentifier smpProcessIdentifier = new SmpProcessIdentifier();
            smpProcessIdentifier.setProcessIdentifierScheme(subscribedProfile.getProcessSchemeSMP());
            smpProcessIdentifier.setProcessIdentifierValue(subscribedProfile.getProcessId());
            serviceData.setProcessIdentifier(smpProcessIdentifier);
            serviceData.setEndpoints(endpointList);
            result.add(serviceData);
        }
        return result;
	}

	private List<SmpServiceEndpointData> createEndpointList(OrganisationSetupData organisationSetup) {
		List<SmpServiceEndpointData> result = new ArrayList<>();
		ArrayList<Long> apIds = Lists.newArrayList(organisationSetup.getAs2(), organisationSetup.getAs4());
		for (Long apId : apIds) {
			if(apId == null) {
				continue;
			}
			AccessPoint accessPoint = accessPointService.findById(apId);
			result.add(toServiceEndpointData(accessPoint));
		}
		return result;
	}

	private SmpServiceEndpointData toServiceEndpointData(AccessPoint accessPoint) {
		SmpServiceEndpointData endpointData = new SmpServiceEndpointData();
		Date serviceActivationDate = null;
		Date serviceExpirationDate = null;
		Blob certBlob = accessPoint.getCertificate();
		byte[] certBytes = null;
		try {
			certBytes = Base64.getDecoder().decode(certBlob.getBytes(1L, (int) certBlob.length()));
			X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
			serviceActivationDate = certificate.getNotBefore();
			serviceExpirationDate = certificate.getNotAfter();
		} catch (CertificateException | SQLException e) {
			log.error(e.getMessage(), e);
		}
		String transportProfile = transportProfilesMap.get(accessPoint.getType());
		endpointData.setTransportProfile(transportProfile);
		endpointData.setUrl(accessPoint.getUrl());
		endpointData.setServiceDescription(accessPoint.getServiceDescription());
		endpointData.setTechnicalContactUrl(accessPoint.getTechnicalContactUrl());
		endpointData.setServiceActivationDate(serviceActivationDate);
		endpointData.setServiceExpirationDate(serviceExpirationDate);
		endpointData.setRequireBusinessLevelSignature(true);
		endpointData.setCertificate(certBytes);
		return endpointData;
	}
}
