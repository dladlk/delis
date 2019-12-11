package eu.domibus.plugin.fs.validation;

import eu.domibus.api.property.DomibusPropertyProvider;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import eu.domibus.plugin.Submission;
import eu.domibus.plugin.validation.SubmissionValidationException;
import eu.domibus.plugin.validation.SubmissionValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component("customCefValidator")
public class ReceiverSubmissionValidator implements SubmissionValidator {

    public static final String REASON_HEADER = "reason";
    protected DomibusPropertyProvider domibusPropertyProvider;

    private static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(ReceiverSubmissionValidator.class);

    public static final String FSPLUGIN_VALIDATION_ENDPOINT = "fsplugin.validation.endpoint";

    public static final String UTF_8 = "utf-8";
    public static final String PARAMETER_SEPARATOR = "/";

    @Autowired
    public ReceiverSubmissionValidator(DomibusPropertyProvider domibusPropertyProvider) {
        this.domibusPropertyProvider = domibusPropertyProvider;
    }

    @Override
    public void validate(Submission submission) throws SubmissionValidationException {
        long startTime = new Date().getTime();
        String messageId = submission.getMessageId();

        LOG.info("Document validation start. Document messageId=" + messageId);


        String endpoint = domibusPropertyProvider.getProperty(FSPLUGIN_VALIDATION_ENDPOINT);
        if (endpoint == null) {
            endpoint = System.getProperty(FSPLUGIN_VALIDATION_ENDPOINT);
        }

        LOG.info("REST endpoint: " + endpoint);

        if (StringUtils.isEmpty(endpoint)) return;

        if (!endpoint.endsWith("/")) {
            endpoint += "/";
        }

        String parameters;
        try {
            parameters = composeParameters(submission);
        } catch (Exception e) {
            throw new SubmissionValidationException(e.getMessage(), e);
        }

        LOG.info("REST parameters string: " + parameters);
        LOG.info("Validation REST start.");
        long startCallTime = new Date().getTime();
        ResponseEntity<String> response = null;
        try {
            response = new RestTemplate(). getForEntity(endpoint + parameters, String.class);
        } catch (RestClientException e) {
            LOG.error("Error during REST call", e);
            throw new SubmissionValidationException(e.getMessage(), e);
        }
        long stopCallTime = new Date().getTime();
        LOG.info("Validation REST end. Execution time: " + (stopCallTime - startCallTime) + " ms.");

        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            String reason = response.getHeaders().get(REASON_HEADER).stream().collect(Collectors.joining(","));
            RuntimeException runtimeException = new RuntimeException(reason);
            throw new SubmissionValidationException("PEPPOL:NOT_SERVICED", runtimeException);
        } else if (response.getStatusCode() != HttpStatus.OK) {
            throw new SubmissionValidationException(response.getStatusCode().getReasonPhrase());
        }

        long stopTime = new Date().getTime();
        LOG.info("Document validation end. Document messageId=" + messageId + ". Execution time: " + (stopTime - startTime) + " ms.");

    }

    public String composeParameters (Submission submission) throws Exception {
        Collection<Submission.TypedProperty> messageProperties = submission.getMessageProperties();

        String finalRecipient = messageProperties.stream().filter(x -> x.getKey().equals("finalRecipient")).findFirst().get().getValue();
        LOG.info("finalRecipient = " + finalRecipient);
        String service = submission.getService();
        LOG.info("service = " + service);
        String action = submission.getAction();
        LOG.info("action = " + action);

        StringBuilder sb = new StringBuilder();
        sb.append(encode(finalRecipient)).append(PARAMETER_SEPARATOR)
                .append(encode(service)).append(PARAMETER_SEPARATOR)
                .append(encode(action));

        return sb.toString();
    }

    private String encode (String source) throws UnsupportedEncodingException {
        return URLEncoder.encode(source, UTF_8);
    }
}
