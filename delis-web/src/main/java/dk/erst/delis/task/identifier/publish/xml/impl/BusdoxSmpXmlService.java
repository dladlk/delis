package dk.erst.delis.task.identifier.publish.xml.impl;

import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import dk.erst.delis.task.identifier.publish.xml.intf.SmpXmlService;
import no.difi.commons.busdox.jaxb.addressing.AttributedURIType;
import no.difi.commons.busdox.jaxb.addressing.EndpointReferenceType;
import no.difi.commons.busdox.jaxb.identifiers.DocumentIdentifierType;
import no.difi.commons.busdox.jaxb.identifiers.ParticipantIdentifierType;
import no.difi.commons.busdox.jaxb.identifiers.ProcessIdentifierType;
import no.difi.commons.busdox.jaxb.smp.*;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class BusdoxSmpXmlService implements SmpXmlService {

    protected final Log log = LogFactory.getLog(getClass());

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final JAXBContext JAXB_CONTEXT = ExceptionUtil.perform(IllegalStateException.class,
            () -> JAXBContext.newInstance(ServiceGroupType.class, ServiceMetadataType.class, SignedServiceMetadataType.class));

    public String createServiceGroupXml(ParticipantIdentifier identifier) {
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

    public String createServiceMetadataXml(ParticipantIdentifier identifier, SmpPublishServiceData smpPublishData) {
        ParticipantIdentifierType participantIdentifierType = buildParticipantIdentifierType(identifier);

        DocumentIdentifierType documentIdentifier = new DocumentIdentifierType();
        documentIdentifier.setScheme(smpPublishData.getDocumentIdentifier().getDocumentIdentifierScheme());
        documentIdentifier.setValue(smpPublishData.getDocumentIdentifier().getDocumentIdentifierValue());

        ServiceInformationType serviceInformation = new ServiceInformationType();
        serviceInformation.setParticipantIdentifier(participantIdentifierType);
        serviceInformation.setDocumentIdentifier(documentIdentifier);

        ProcessListType processListType = new ProcessListType();
        List<ProcessType> processList = processListType.getProcess();
        processList.addAll(createProcessList(smpPublishData));
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

    private List<ProcessType> createProcessList(SmpPublishServiceData smpPublishData) {
        ArrayList<ProcessType> result = new ArrayList<>();

        ProcessType process = new ProcessType();
        ProcessIdentifierType processIdentifier = new ProcessIdentifierType();
        processIdentifier.setScheme(smpPublishData.getProcessIdentifier().getProcessIdentifierScheme());
        processIdentifier.setValue(smpPublishData.getProcessIdentifier().getProcessIdentifierValue());
        process.setProcessIdentifier(processIdentifier);

        ServiceEndpointList serviceEndpoint = new ServiceEndpointList();
        List<EndpointType> endpointList = serviceEndpoint.getEndpoint();
        List<SmpServiceEndpointData> endpoints = smpPublishData.getEndpoints();

        for (SmpServiceEndpointData endpoint : endpoints) {
            EndpointType resultEndpoint = new EndpointType();
            resultEndpoint.setRequireBusinessLevelSignature(endpoint.isRequireBusinessLevelSignature());
            resultEndpoint.setCertificate(endpoint.getCertificateBase64());
            resultEndpoint.setTransportProfile(endpoint.getTransportProfile());
            resultEndpoint.setEndpointReference(createEndpointReference(endpoint));
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

    private EndpointReferenceType createEndpointReference(SmpServiceEndpointData endpoint) {
        AttributedURIType address = new AttributedURIType();
        address.setValue(endpoint.getUrl());
        EndpointReferenceType endpointReferenceType = new EndpointReferenceType();
        endpointReferenceType.setAddress(address);
        return endpointReferenceType;
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
                this.stringBuilder.append((char) b);
            }

            public String toString() {
                return this.stringBuilder.toString();
            }
        };
    }
}
