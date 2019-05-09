package dk.erst.delis.task.identifier.publish.xml;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.config.SmpEndpointConfig;
import dk.erst.delis.task.identifier.publish.xml.impl.BdxrSmpXmlService;
import dk.erst.delis.task.identifier.publish.xml.impl.BusdoxSmpXmlService;
import dk.erst.delis.task.identifier.publish.xml.intf.SmpXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmpXmlServiceFactory {

    private final ConfigBean configBean;

    @Autowired
    public SmpXmlServiceFactory(ConfigBean configBean) {
        this.configBean = configBean;
    }

    public SmpXmlService createInstance() {
        SmpEndpointConfig smpEndpointConfig = configBean.getSmpEndpointConfig();
        if("OASIS".equalsIgnoreCase(smpEndpointConfig.getFormat())) {
            return new BdxrSmpXmlService();
        }
        return new BusdoxSmpXmlService();
    }

}
