package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.data.SmpEndpoint;
import dk.erst.delis.xml.SmpXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifierPublishService {

    private final SmpXmlService smpXmlService;

    @Autowired
    public IdentifierPublishService(SmpXmlService smpXmlService) {
        this.smpXmlService = smpXmlService;
    }

    public boolean publish(Identifier identifier, SmpEndpoint endpoint){
        String serviceGroupXml = smpXmlService.createServiceGroupXml(identifier);
        String serviceMetadataXml = smpXmlService.createServiceMetadataXml(identifier);

        return true;
    }
}
