package eu.domibus.plugin.fs.validation;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import eu.domibus.api.property.DomibusPropertyProvider;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import eu.domibus.plugin.Submission;
import eu.domibus.plugin.Submission.Payload;
import eu.domibus.plugin.validation.SubmissionValidationException;
import eu.domibus.plugin.validation.SubmissionValidator;

@Component("delisSumbissionValidator")
public class DelisSubmissionValidator implements SubmissionValidator {

	public static final String REASON_HEADER = "reason";
	private DomibusPropertyProvider domibusPropertyProvider;

	private static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(DelisSubmissionValidator.class);

	public static final String FSPLUGIN_SERVICE_VALIDATION_ENDPOINT = "fsplugin.validation.endpoint";
	public static final String FSPLUGIN_XML_VALIDATION_ENDPOINT = "fsplugin.payload.validation.endpoint";

	public static final String UTF_8 = "utf-8";
	public static final String PARAMETER_SEPARATOR = "/";

	@Autowired
	public DelisSubmissionValidator(DomibusPropertyProvider domibusPropertyProvider) {
		this.domibusPropertyProvider = domibusPropertyProvider;
	}

	@Override
	public void validate(Submission submission) throws SubmissionValidationException {
		long startTime = System.currentTimeMillis();
		String messageId = submission.getMessageId();

		try {
			LOG.info("DelisSubmissionValidator: START #" + messageId);

			validateService(submission);
			validatePayload(submission);

		} finally {
			LOG.info("DelisSubmissionValidator: DONE #" + messageId + " in " + (System.currentTimeMillis() - startTime));
		}
	}

	private void validatePayload(Submission submission) throws SubmissionValidationException {
		String xmlValidationEndpoint = getValidationEndpoint(FSPLUGIN_XML_VALIDATION_ENDPOINT, false);
		if (xmlValidationEndpoint == null) {
			return;
		}
		long startCallTime = System.currentTimeMillis();

		int responseStatusCode = -1;
		String responseBody = null;
		String reasonCode = null;
		try {
			ResponseEntity<String> response = null;
			Payload payload = submission.getPayloads().iterator().next();

			LOG.info("DelisSubmissionValidator: CALL " + xmlValidationEndpoint);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", new File(payload.getFilepath()));
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);
			response = new RestTemplate().postForEntity(xmlValidationEndpoint, requestEntity, String.class);
			reasonCode = response.getHeaders().get(REASON_HEADER).stream().collect(Collectors.joining(","));
			responseStatusCode = response.getStatusCodeValue();
			responseBody = response.getBody();
		} catch (Exception e) {
			LOG.error("DelisSubmissionValidator: CALL FAILED to " + xmlValidationEndpoint + ", consider result as valid", e);
			return;
		} finally {
			LOG.info("DelisSubmissionValidator: CALL DONE in " + (System.currentTimeMillis() - startCallTime) + " with status " + responseStatusCode + ", reasonCode " + reasonCode + ", body " + responseBody);
		}

		if (responseStatusCode == 412) {
			RuntimeException runtimeException = new RuntimeException(responseBody);
			throw new SubmissionValidationException(reasonCode, runtimeException);
		} else if (responseStatusCode != 200) {
			LOG.error("eceiverSubmissionValidator: Payload validation service returned unexpected HTTP code " + responseStatusCode + ", consider payload as valid");
		}
	}

	private void validateService(Submission submission) throws SubmissionValidationException {
		String serviceValidationEndpoint = getValidationEndpoint(FSPLUGIN_SERVICE_VALIDATION_ENDPOINT, true);
		if (serviceValidationEndpoint == null) {
			return;
		}
		String serviceValidationUrl = serviceValidationEndpoint + composeParameters(submission);

		long startCallTime = System.currentTimeMillis();
		ResponseEntity<String> response = null;
		try {
			LOG.info("DelisSubmissionValidator: CALL " + serviceValidationUrl);
			response = new RestTemplate().getForEntity(serviceValidationUrl, String.class);
		} catch (Exception e) {
			LOG.error("Error during REST call to " + serviceValidationUrl + ", consider result as valid", e);
			return;
		} finally {
			LOG.info("Validation REST end. Execution time: " + (System.currentTimeMillis() - startCallTime) + " ms.");
		}

		if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
			String reason = response.getHeaders().get(REASON_HEADER).stream().collect(Collectors.joining(","));
			RuntimeException runtimeException = new RuntimeException(reason);
			throw new SubmissionValidationException("PEPPOL:NOT_SERVICED", runtimeException);
		}
	}

	private String getValidationEndpoint(String propertyName, boolean ensureLastSlash) {
		String serviceValidationEndpoint = domibusPropertyProvider.getProperty(propertyName);
		if (serviceValidationEndpoint == null) {
			serviceValidationEndpoint = System.getProperty(propertyName);
		}

		if (StringUtils.isEmpty(serviceValidationEndpoint)) {
			return null;
		}

		if (ensureLastSlash) {
			if (!serviceValidationEndpoint.endsWith("/")) {
				serviceValidationEndpoint += "/";
			}
		}
		return serviceValidationEndpoint;
	}

	protected String composeParameters(Submission submission) {
		Collection<Submission.TypedProperty> messageProperties = submission.getMessageProperties();

		String finalRecipient = messageProperties.stream().filter(x -> x.getKey().equals("finalRecipient")).findFirst().get().getValue();
		LOG.info("finalRecipient = " + finalRecipient);
		/*
		 * Convert finalRecipient iso6523-actorid-upis::0088:5798009882875 to 0088:5798009882875
		 */
		finalRecipient = removeSchemaPrefix(finalRecipient, "finalRecipient", null);
		String service = submission.getService();
		LOG.info("service = " + service);
		/*
		 * Convert service
		 * 
		 * nes-procid-ubl::urn:www.nesubl.eu:profiles:profile5:ver2.0 to
		 * 
		 * urn:www.nesubl.eu:profiles:profile5:ver2.0 and
		 * 
		 * cenbii-procid-ubl::urn:fdc:peppol.eu:2017:poacc:billing:01:1.0 to
		 * 
		 * urn:fdc:peppol.eu:2017:poacc:billing:01:1.0
		 */
		service = removeSchemaPrefix(service, "service", null);
		String action = submission.getAction();
		LOG.info("action = " + action);
		/*
		 * Convert action only if busdox-docid-qns::
		 */
		action = removeSchemaPrefix(action, "action", "busdox-docid-qns");

		StringBuilder sb = new StringBuilder();
		sb.append(encode(finalRecipient));
		sb.append(PARAMETER_SEPARATOR);
		sb.append(encode(service));
		sb.append(PARAMETER_SEPARATOR);
		sb.append(encode(action));

		return sb.toString();
	}

	private String removeSchemaPrefix(String value, String fieldName, String prefixToRemove) {
		if (value == null) {
			return value;
		}
		int startValue = value.indexOf("::");
		if (startValue >= 0 && value.length() > startValue + 2) {
			String prefix = value.substring(0, startValue);
			if (prefixToRemove == null || prefixToRemove.equals(prefix)) {
				value = value.substring(startValue + 2);
				LOG.info("Field " + fieldName + " transformed to " + value);
			}
		}
		return value;
	}

	private String encode(String source) {
		/*
		 * RestTemplate ALREADY encodes URL, if we encode it here - it is DOUBLE encoded... But let's keep it
		 */
		try {
			return URLEncoder.encode(source, UTF_8);
		} catch (UnsupportedEncodingException e) {
			/*
			 * UTF-8 is always supported
			 */
			return source;
		}
	}
}
