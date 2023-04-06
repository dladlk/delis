package dk.erst.delis.task.identifier.publish;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpProcessIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishProcessData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.common.lang.PeppolLoadingException;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessMetadata;
import network.oxalis.vefa.peppol.common.model.ServiceInformation;
import network.oxalis.vefa.peppol.common.model.ServiceMetadata;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import network.oxalis.vefa.peppol.lookup.LookupClient;
import network.oxalis.vefa.peppol.lookup.LookupClientBuilder;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.MetadataLocator;
import network.oxalis.vefa.peppol.lookup.provider.DefaultProvider;
import network.oxalis.vefa.peppol.security.api.CertificateValidator;
import network.oxalis.vefa.peppol.security.lang.PeppolSecurityException;

@Service
@Slf4j
public class SmpLookupService {

	private ConfigBean configBean;

	@Autowired
	public SmpLookupService(ConfigBean configBean) {
		this.configBean = configBean;
	}
	
	public SmpPublishData lookup(ParticipantIdentifier identifier) {
		return lookup(identifier, true);
	}
	
	public SmpPublishData lookup(ParticipantIdentifier identifier, boolean useConfiguredSmp) {
		SmpPublishData smpPublishData = new SmpPublishData();
		if (useConfiguredSmp) {
			log.info("Performing lookup for published data by ParticipantIdentifier "+identifier+" at SMP "+configBean.getSmpEndpointConfig().getUrl());
		} else {
			log.info("Performing lookup for published data by ParticipantIdentifier "+identifier+" at SML");
		}
		try {
			LookupClient client = createLookupClient(useConfiguredSmp);
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

	private LookupClient createLookupClient(boolean useConfiguredSmp) throws PeppolLoadingException {
		if (!useConfiguredSmp) {
			return LookupClientBuilder.forProduction().build();
		}
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
            public List<URI> resolveDocumentIdentifiers(URI location, ParticipantIdentifier participant) {
                URI newUri = null;
                try {
                    newUri = new URI(location.toString()+ "/"+participant.urlencoded());
                } catch (Exception e) {
					log.error("Failed to build URI for location=" + location + ", participant=" + participant);
                }
                log.info("Resolved URI to document identifiers: "+newUri);
                return Arrays.asList(new URI[] { newUri });
            }
            @Override
            public List<URI> resolveServiceMetadata(URI location, ParticipantIdentifier participantIdentifier, DocumentTypeIdentifier documentTypeIdentifier) {
            	URI result = location;
                try {
                    result = new URI(location.toString()+String.format("/%s/services/%s", participantIdentifier.urlencoded(), documentTypeIdentifier.urlencoded()));
                } catch (Exception e) {
					log.error("Failed to build URI for location=" + location + ", participant=" + participantIdentifier + ", documentTypeIdentifier=" + documentTypeIdentifier);
                }
				log.info("Resolved URI to service metadata: " + result);
				return Arrays.asList(new URI[] { result });
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
			smpPublishServiceData.setProcessList(new ArrayList<SmpPublishProcessData>());
			
			ServiceInformation<Endpoint> serviceInformation = serviceMetadata.getServiceInformation();
			DocumentTypeIdentifier documentTypeIdentifier = serviceInformation.getDocumentTypeIdentifier();
			SmpDocumentIdentifier documentIdentifier = SmpDocumentIdentifier.of(documentTypeIdentifier.getIdentifier());
			smpPublishServiceData.setDocumentIdentifier(documentIdentifier);
			for (ProcessMetadata<Endpoint> processMetadata : serviceInformation.getProcesses()) {
				if(processMetadata.getProcessIdentifier().isEmpty()) {
					continue;
				}
				SmpPublishProcessData processData = new SmpPublishProcessData();
				
				SmpProcessIdentifier smpProcessIdentifier = new SmpProcessIdentifier();
				ProcessIdentifier processIdentifier = processMetadata.getProcessIdentifier().get(0);
				smpProcessIdentifier.setProcessIdentifierScheme(processIdentifier.getScheme().toString());
				smpProcessIdentifier.setProcessIdentifierValue(processIdentifier.getIdentifier());
				processData.setProcessIdentifier(smpProcessIdentifier);
				processData.setEndpoints(createEndpoints(processMetadata.getEndpoints()));
				
				smpPublishServiceData.getProcessList().add(processData);
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
