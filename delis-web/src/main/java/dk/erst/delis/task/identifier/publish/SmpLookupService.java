package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.difi.vefa.peppol.lookup.LookupClientBuilder;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SmpLookupService {

	private ConfigBean configBean;

	@Autowired
	public SmpLookupService(ConfigBean configBean) {
		this.configBean = configBean;
	}
	
	public SmpPublishData lookup(ParticipantIdentifier identifier) {
		SmpPublishData smpPublishData = new SmpPublishData();
		log.info("Performing lookup for publish data by ParticipantIdentifier "+identifier);
		try {
			LookupClient client = LookupClientBuilder.forTest().build();
			List<DocumentTypeIdentifier> documentIdentifiers = client.getDocumentIdentifiers(identifier);
			log.info(String.format("%d DocumentIdentifiers found by ParticipantIdentifier %s", documentIdentifiers.size(), identifier));
			List<ServiceMetadata> serviceMetadataList = queryServiceMetaData(identifier, client, documentIdentifiers);
			smpPublishData.setServiceList(createServiceList(serviceMetadataList));
			smpPublishData.setParticipantIdentifier(identifier);
		} catch (PeppolLoadingException | LookupException | PeppolSecurityException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return smpPublishData;
	}

	private List<ServiceMetadata> queryServiceMetaData(ParticipantIdentifier identifier, LookupClient client, List<DocumentTypeIdentifier> documentIdentifiers) throws LookupException, PeppolSecurityException {
		List<ServiceMetadata> serviceMetadataList = new ArrayList<>();
		for (DocumentTypeIdentifier documentIdentifier : documentIdentifiers) {
            ServiceMetadata serviceMetadata = client.getServiceMetadata(identifier, documentIdentifier);
            serviceMetadataList.add(serviceMetadata);
        }
		return serviceMetadataList;
	}

	private List<SmpPublishServiceData> createServiceList(List<ServiceMetadata> serviceMetadataList) {
		List<SmpPublishServiceData> serviceDataList = new ArrayList<>();
		for (ServiceMetadata serviceMetadata : serviceMetadataList) {
			SmpPublishServiceData smpPublishServiceData = new SmpPublishServiceData();
			DocumentTypeIdentifier documentTypeIdentifier = serviceMetadata.getDocumentTypeIdentifier();
			SmpDocumentIdentifier documentIdentifier = SmpDocumentIdentifier.of(documentTypeIdentifier.getIdentifier());
			smpPublishServiceData.setDocumentIdentifier(documentIdentifier);
			//TODO add values
//			smpPublishServiceData.setProcessIdentifier();
//			smpPublishServiceData.setEndpoints();
			serviceDataList.add(smpPublishServiceData);
		}
		return serviceDataList;
	}
}
