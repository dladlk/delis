package dk.erst.delis.task.identifier.publish.xml.intf;

import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

public interface SmpXmlService {
    String createServiceMetadataXml(ParticipantIdentifier identifier, SmpPublishServiceData smpPublishData);
    String createServiceGroupXml(ParticipantIdentifier identifier);
}
