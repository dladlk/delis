package dk.erst.delis.xml;

import dk.erst.delis.data.Identifier;
import no.difi.commons.bdx.jaxb.smp._2016._05.*;
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
import java.util.*;

@Service
public class SmpXmlService {

    protected final Log log = LogFactory.getLog(getClass());

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final JAXBContext JAXB_CONTEXT = ExceptionUtil.perform(IllegalStateException.class,
            () -> JAXBContext.newInstance(ServiceGroupType.class, ServiceMetadataType.class, SignedServiceMetadataType.class));

    public String createServiceGroupXml(Identifier identifier) {
        ParticipantIdentifierType participantIdentifier = new ParticipantIdentifierType();
        participantIdentifier.setScheme(identifier.getType());
        participantIdentifier.setValue(identifier.getValue());
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

    public String createServiceMetadataXml(Identifier identifier) {
        ParticipantIdentifierType participantIdentifierType = new ParticipantIdentifierType();
        String identifierScheme = identifier.getType();
        String identifierValue = identifier.getValue();
        participantIdentifierType.setScheme(identifierScheme);
        participantIdentifierType.setValue(identifierValue);
        DocumentIdentifierType documentIdentifier = new DocumentIdentifierType();
        String doctypeScheme = "connectivity-docid-qns";
        String doctype = "doc_id1";
        documentIdentifier.setScheme(doctypeScheme);
        documentIdentifier.setValue(doctype);
        ServiceInformationType serviceInformation = new ServiceInformationType();
        serviceInformation.setParticipantIdentifier(participantIdentifierType);
        serviceInformation.setDocumentIdentifier(documentIdentifier);
        ProcessListType processListType = new ProcessListType();
        List<ProcessType> processList = processListType.getProcess();
        processList.addAll(createProcessList(identifier));
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

    private List<ProcessType> createProcessList(Identifier identifier) {
        ArrayList<ProcessType> result = new ArrayList<>();
        String processScheme = "connectivity-procid-qns";
        String processValue = "urn:www.cenbii.eu:profile:bii04:ver1.0";
        String certificateBase64String = "dGVzdA==";
        String transportProfile = "bdxr-transport-ebms3-as4-v1p0";
        String endpointUrl = "http://localhost:8080";
        String serviceDescription = "Test service description";
        Date serviceActivationDate = new Date();
        Date serviceExpirationDate = new Date();
        String technicalContactUrl = "http://example.com";
        ProcessType process = new ProcessType();
        ProcessIdentifierType processIdentifier = new ProcessIdentifierType();
        processIdentifier.setScheme(processScheme);
        processIdentifier.setValue(processValue);
        process.setProcessIdentifier(processIdentifier);
        ServiceEndpointList serviceEndpoint = new ServiceEndpointList();
        List<EndpointType> endpointList = serviceEndpoint.getEndpoint();
        EndpointType endpoint = new EndpointType();
        byte[] certificateBytes = Base64.getDecoder().decode(certificateBase64String);
        endpoint.setRequireBusinessLevelSignature(true);
        endpoint.setCertificate(certificateBytes);
        endpoint.setTransportProfile(transportProfile);
        endpoint.setEndpointURI(endpointUrl);
        endpoint.setServiceDescription(serviceDescription);
        endpoint.setServiceActivationDate(toXMLGregorianCalendar(serviceActivationDate));
        endpoint.setServiceExpirationDate(toXMLGregorianCalendar(serviceExpirationDate));
        endpoint.setTechnicalContactUrl(technicalContactUrl);
        endpointList.add(endpoint);
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
