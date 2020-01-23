package eu.domibus.plugin.fs.validation;

import static eu.domibus.plugin.fs.validation.ReceiverSubmissionValidator.FSPLUGIN_VALIDATION_ENDPOINT;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.domibus.api.property.DomibusPropertyProvider;
import eu.domibus.plugin.Submission;
import eu.domibus.plugin.fs.FSMessage;
import eu.domibus.plugin.fs.FSMessageTransformer;
import eu.domibus.plugin.fs.FSPayload;
import eu.domibus.plugin.fs.FSTestHelper;
import eu.domibus.plugin.fs.ebms3.ObjectFactory;
import eu.domibus.plugin.fs.ebms3.UserMessage;
import eu.domibus.plugin.validation.SubmissionValidationException;

public class FSValidatorTest {

    public static final String UTF_8 = "utf-8";

    @Test
    public void testUrlCompose () throws Exception {
    	runTestCase("metadata.xml");
    	runTestCase("metadata2.xml");
    }

	private void runTestCase(String resourceName) throws Exception {
		File tempDirectory = Files.createTempDirectory(this.getClass().getSimpleName()).toFile();
        File metadataFile = null;
        try {
			metadataFile = FSTestHelper.copyResource(this.getClass(), tempDirectory, resourceName);
            UserMessage metadata = getUserMessage(metadataFile);
            final Map<String, FSPayload> fsPayloads = new HashMap<>();
            FSMessage fsMessage = new FSMessage(fsPayloads, metadata);
            FSMessageTransformer transformer = new FSMessageTransformer();
            Submission submission = transformer.transformToSubmission(fsMessage);

            ReceiverSubmissionValidator validator = new ReceiverSubmissionValidator(Mockito.mock(DomibusPropertyProvider.class));
            String parameters = validator.composeParameters(submission);
            
            
            String[] split = parameters.split("/");

            Assert.assertEquals("0088%3A5790001968526/urn%3Awww.nesubl.eu%3Aprofiles%3Aprofile5%3Aver2.0/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AInvoice-2%3A%3AInvoice%23%23OIOUBL-2.02%3A%3A2.0", parameters);
            Assert.assertEquals(3, split.length);
            Assert.assertEquals("0088:5790001968526", URLDecoder.decode(split[0], UTF_8));
            Assert.assertEquals("urn:www.nesubl.eu:profiles:profile5:ver2.0", URLDecoder.decode(split[1], UTF_8));
            Assert.assertEquals("busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##OIOUBL-2.02::2.0", URLDecoder.decode(split[2], UTF_8));

        } finally {
            metadataFile.delete();
            tempDirectory.delete();
        }
	}

//    @Test
    public void testFully () throws Exception { // requires Delise to be run (see. FSPLUGIN_VALIDATION_ENDPOINT value)

        System.setProperty(FSPLUGIN_VALIDATION_ENDPOINT, "http://localhost:8085/delis/rest/open/receivercheck/");

        File tempDirectory = Files.createTempDirectory(this.getClass().getSimpleName()).toFile();
        File metadataFile = null;
        try {
            metadataFile = FSTestHelper.copyResource(this.getClass(), tempDirectory, "metadata.xml");
            UserMessage metadata = getUserMessage(metadataFile);
            final Map<String, FSPayload> fsPayloads = new HashMap<>();
            FSMessage fsMessage = new FSMessage(fsPayloads, metadata);
            FSMessageTransformer transformer = new FSMessageTransformer();
            Submission submission = transformer.transformToSubmission(fsMessage);

            ReceiverSubmissionValidator validator = new ReceiverSubmissionValidator(Mockito.mock(DomibusPropertyProvider.class));

            try {
                validator.validate(submission);
            } catch (SubmissionValidationException e) {
                Assert.fail(e.getMessage());
            }


        } finally {
            metadataFile.delete();
            tempDirectory.delete();
        }
    }


    public static UserMessage getUserMessage(File file) throws Exception {
        InputStream metadata = new FileInputStream(file);
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();
        StreamSource streamSource = new StreamSource(metadata);
        JAXBElement<UserMessage> jaxbElement = um.unmarshal(streamSource, UserMessage.class);
        return jaxbElement.getValue();
    }
}
