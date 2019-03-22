package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.codelist.CodeListReaderService;
import dk.erst.delis.task.identifier.publish.xml.SmpXmlServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class IdentifierPublishServiceTest {

    @Autowired
    private ConfigValueDaoRepository configRepository;

    @Test
    public void test() {

        ConfigBean configBean = new ConfigBean(configRepository);
        SmpXmlServiceFactory smpXmlServiceFactory = new SmpXmlServiceFactory(configBean);
        SmpIntegrationService smpIntegrationService = new SmpIntegrationService(configBean);
        CodeListDict codeListDict = new CodeListDict(new CodeListReaderService(configBean));
        IdentifierPublishDataService identifierPublishDataService = new IdentifierPublishDataService(codeListDict);
        SmpLookupService smpLookupService = new SmpLookupService(configBean);

        IdentifierPublishService publishService = new IdentifierPublishService(smpXmlServiceFactory, smpIntegrationService, identifierPublishDataService, smpLookupService);


    }

}
