package dk.erst.delis.task.identifier.publish;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpProcessIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.ProcessMetadata;
import no.difi.vefa.peppol.common.model.ServiceMetadata;
import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.difi.vefa.peppol.lookup.LookupClientBuilder;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.difi.vefa.peppol.lookup.api.MetadataLocator;
import no.difi.vefa.peppol.lookup.provider.DefaultProvider;
import no.difi.vefa.peppol.security.api.CertificateValidator;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;

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
		log.info("Performing lookup for published data by ParticipantIdentifier "+identifier+" at SMP "+configBean.getSmpEndpointConfig().getUrl());
		try {
			LookupClient client = createLookupClient();
			List<DocumentTypeIdentifier> documentIdentifiers = client.getDocumentIdentifiers(identifier);
			log.info(String.format("%d DocumentIdentifiers found by ParticipantIdentifier %s", documentIdentifiers.size(), identifier));
			List<ServiceMetadata> serviceMetadataList = queryServiceMetaData(identifier, client, documentIdentifiers);
			smpPublishData.setServiceList(createServiceList(serviceMetadataList));
			smpPublishData.setParticipantIdentifier(identifier);
		} catch (PeppolLoadingException | PeppolSecurityException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (LookupException le) {
			/*
			 * LookupException means that we did not find any record in configured SMP - do not log exception in this case
			 */
			log.info(String.format("No data found for ParticipantIdentifier %s", identifier));
			return null;
		}
		return smpPublishData;
	}

	private LookupClient createLookupClient() throws PeppolLoadingException {
		return LookupClientBuilder.forProduction()
                        .locator(createLocalSMPLocator())
                        .provider(createMetadataProvider())
						.certificateValidator(createCertificateValidator())
                        .build();
	}

	private CertificateValidator createCertificateValidator() {
		return (service, x509Certificate) -> {
        };
	}

	private DefaultProvider createMetadataProvider() {
		return new DefaultProvider(){
            @Override
            public URI resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
                URI newUri = null;
                try {
                    newUri = new URI(location.toString()+ "/"+participant.urlencoded());
                } catch (Exception e) {
					log.error("Failed to build URI for location=" + location + ", participant=" + participant);
                }
                log.info("Resolved URI to document identifiers: "+newUri);
                return newUri;
            }
            @Override
            public URI resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier) {
            	URI result = location;
                try {
                    result = new URI(location.toString()+String.format("/%s/services/%s", participantIdentifier.urlencoded(), documentTypeIdentifier.urlencoded()));
                } catch (Exception e) {
					log.error("Failed to build URI for location=" + location + ", participant=" + participantIdentifier + ", documentTypeIdentifier=" + documentTypeIdentifier);
                }
				log.info("Resolved URI to service metadata: " + result);
                return result;
            }
        };
	}

	private MetadataLocator createLocalSMPLocator() {
		return new MetadataLocator() {
            @Override
            public URI lookup(String identifier) throws LookupException {
				try {
					return new URI(configBean.getSmpEndpointConfig().getUrl());
				} catch (URISyntaxException e) {
					throw new LookupException(e.getMessage());
				}
			}
            @Override
            public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
				try {
					return new URI(configBean.getSmpEndpointConfig().getUrl());
				} catch (URISyntaxException e) {
					throw new LookupException(e.getMessage());
				}
			}
        };
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
			for (ProcessMetadata<Endpoint> processMetadata : serviceMetadata.getProcesses()) {
				if(processMetadata.getProcessIdentifier().isEmpty()) {
					continue;
				}
				SmpProcessIdentifier smpProcessIdentifier = new SmpProcessIdentifier();
				ProcessIdentifier processIdentifier = processMetadata.getProcessIdentifier().get(0);
				smpProcessIdentifier.setProcessIdentifierScheme(processIdentifier.getScheme().toString());
				smpProcessIdentifier.setProcessIdentifierValue(processIdentifier.getIdentifier());
				smpPublishServiceData.setProcessIdentifier(smpProcessIdentifier);
				smpPublishServiceData.setEndpoints(createEndpoints(processMetadata.getEndpoints()));
			}
			serviceDataList.add(smpPublishServiceData);
		}
		return serviceDataList;
	}

	private List<SmpServiceEndpointData> createEndpoints(List<Endpoint> endpoints) {
		List<SmpServiceEndpointData> result = new ArrayList<>();
		for (Endpoint endpoint : endpoints) {
			URI address = endpoint.getAddress();
			TransportProfile transportProfile = endpoint.getTransportProfile();
			//TODO investigate why period is NULL
			//Period period = endpoint.getPeriod(); 
			X509Certificate certificate = endpoint.getCertificate();
			SmpServiceEndpointData endpointData = new SmpServiceEndpointData();
			endpointData.setUrl(address.toString());
			endpointData.setTransportProfile(transportProfile.getIdentifier());
			try {
				endpointData.setCertificate(certificate.getEncoded());
			} catch (CertificateEncodingException e) {
				log.error(e.getMessage(), e);
			}
//			endpointData.setServiceActivationDate(period.getFrom());
//			endpointData.setServiceExpirationDate(period.getTo());
//			endpointData.setRequireBusinessLevelSignature(??);
//			endpointData.setServiceDescription(??);
//			endpointData.setTechnicalContactUrl(??);
			result.add(endpointData);
		}
		return result;
	}
}
