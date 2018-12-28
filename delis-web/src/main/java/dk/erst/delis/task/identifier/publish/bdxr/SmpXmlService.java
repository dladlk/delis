package dk.erst.delis.task.identifier.publish.bdxr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import dk.erst.delis.task.identifier.publish.PublishProperties;
import dk.erst.delis.task.identifier.publish.ServiceEndpoint;
import no.difi.commons.bdx.jaxb.smp._2016._05.DocumentIdentifierType;
import no.difi.commons.bdx.jaxb.smp._2016._05.EndpointType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ObjectFactory;
import no.difi.commons.bdx.jaxb.smp._2016._05.ParticipantIdentifierType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ProcessIdentifierType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ProcessListType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ProcessType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ServiceEndpointList;
import no.difi.commons.bdx.jaxb.smp._2016._05.ServiceGroupType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ServiceInformationType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ServiceMetadataReferenceCollectionType;
import no.difi.commons.bdx.jaxb.smp._2016._05.ServiceMetadataType;
import no.difi.commons.bdx.jaxb.smp._2016._05.SignedServiceMetadataType;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.util.ExceptionUtil;

@Service
public class SmpXmlService {

    protected final Log log = LogFactory.getLog(getClass());

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final JAXBContext JAXB_CONTEXT = ExceptionUtil.perform(IllegalStateException.class,
            () -> JAXBContext.newInstance(ServiceGroupType.class, ServiceMetadataType.class, SignedServiceMetadataType.class));

    public String createServiceGroupXml(ParticipantIdentifier identifier, PublishProperties publishProperties) {
        ParticipantIdentifierType participantIdentifier = buildParticipantIdentifierType(identifier);
        
        ServiceGroupType serviceGroup = new ServiceGroupType();
        serviceGroup.setParticipantIdentifier(participantIdentifier);
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());
        
        OutputStream result = createResultStream();
        try {
            JAXB_CONTEXT.createMarshaller().marshal(OBJECT_FACTORY.createServiceGroup(serviceGroup), result);
        } catch (JAXBException e) {
            log.error(e);
        }
        return result.toString();
    }

	private ParticipantIdentifierType buildParticipantIdentifierType(ParticipantIdentifier identifier) {
		ParticipantIdentifierType participantIdentifier = new ParticipantIdentifierType();
        participantIdentifier.setScheme(identifier.getScheme().getIdentifier());
        participantIdentifier.setValue(identifier.getIdentifier());
		return participantIdentifier;
	}

    public String createServiceMetadataXml(ParticipantIdentifier identifier, PublishProperties publishProperties) {
        ParticipantIdentifierType participantIdentifierType = buildParticipantIdentifierType(identifier);
        
        DocumentIdentifierType documentIdentifier = new DocumentIdentifierType();
        documentIdentifier.setScheme(publishProperties.getDocumentIdentifierScheme());
        documentIdentifier.setValue(publishProperties.getDocumentIdentifierValue());
        
        ServiceInformationType serviceInformation = new ServiceInformationType();
        serviceInformation.setParticipantIdentifier(participantIdentifierType);
        serviceInformation.setDocumentIdentifier(documentIdentifier);
        
        ProcessListType processListType = new ProcessListType();
        List<ProcessType> processList = processListType.getProcess();
        processList.addAll(createProcessList(publishProperties));
        serviceInformation.setProcessList(processListType);
        
        ServiceMetadataType serviceMetadata = new ServiceMetadataType();
        serviceMetadata.setServiceInformation(serviceInformation);
        
        OutputStream result = createResultStream();
        try {
            JAXB_CONTEXT.createMarshaller().marshal(OBJECT_FACTORY.createServiceMetadata(serviceMetadata), result);
        } catch (JAXBException e) {
            log.error(e);
        }
        return result.toString();
    }

    private List<ProcessType> createProcessList(PublishProperties publishProperties) {
        ArrayList<ProcessType> result = new ArrayList<>();
        
        ProcessType process = new ProcessType();
        ProcessIdentifierType processIdentifier = new ProcessIdentifierType();
        processIdentifier.setScheme(publishProperties.getProcessIdentifierScheme());
        processIdentifier.setValue(publishProperties.getProcessIdentifierValue());
        process.setProcessIdentifier(processIdentifier);
        
        ServiceEndpointList serviceEndpoint = new ServiceEndpointList();
        List<EndpointType> endpointList = serviceEndpoint.getEndpoint();
        List<ServiceEndpoint> endpoints = publishProperties.getEndpoints();
        
        for (ServiceEndpoint endpoint : endpoints) {
            EndpointType resultEndpoint = new EndpointType();
            resultEndpoint.setRequireBusinessLevelSignature(endpoint.isRequireBusinessLevelSignature());
            resultEndpoint.setCertificate(endpoint.getCertificate());
            resultEndpoint.setTransportProfile(endpoint.getTransportProfile());
            resultEndpoint.setEndpointURI(endpoint.getUrl());
            resultEndpoint.setServiceDescription(endpoint.getServiceDescription());
            resultEndpoint.setServiceActivationDate(toXMLGregorianCalendar(endpoint.getServiceActivationDate()));
            resultEndpoint.setServiceExpirationDate(toXMLGregorianCalendar(endpoint.getServiceExpirationDate()));
            resultEndpoint.setTechnicalContactUrl(endpoint.getTechnicalContactUrl());
            endpointList.add(resultEndpoint);
        }
        process.setServiceEndpointList(serviceEndpoint);
        result.add(process);
        return result;
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        XMLGregorianCalendar result = null;
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            log.error(e);
        }
        return result;
    }

    private OutputStream createResultStream() {
        return new OutputStream() {
            private StringBuilder stringBuilder = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                this.stringBuilder.append((char) b );
            }
            public String toString(){
                return this.stringBuilder.toString();
            }
        };
    }
}