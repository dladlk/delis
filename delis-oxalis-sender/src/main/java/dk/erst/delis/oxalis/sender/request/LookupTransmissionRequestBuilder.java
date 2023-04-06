package dk.erst.delis.oxalis.sender.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import dk.erst.delis.document.sbdh.AlreadySBDHException;
import dk.erst.delis.document.sbdh.SBDHTranslator;
import dk.erst.delis.oxalis.sender.ISendListener;
import dk.erst.delis.oxalis.sender.SendStep;
import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.api.lang.OxalisTransmissionException;
import network.oxalis.api.lookup.LookupService;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.sbdh.SbdReader;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;

@Slf4j
public class LookupTransmissionRequestBuilder implements IDelisTransmissionRequestBuilder {

	private LookupService lookupService;

	public LookupTransmissionRequestBuilder(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public DelisTransmissionRequest build(InputStream payload, Optional<ISendListener> listener) throws IOException, TransmissionLookupException, SbdhException {
		listener.orElse(ISendListener.NONE).notifySendStepStart(SendStep.SBDH);

		SBDHTranslator t = new SBDHTranslator();
		Header header;
		InputStream sbdhStream;
		try {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				header = t.addHeader(payload, baos);
				payload.close();
				sbdhStream = new ByteArrayInputStream(baos.toByteArray());
			} catch (AlreadySBDHException e) {
				log.info("Payload already includes SBDH, reuse it");
				payload.reset();
				SbdReader reader = SbdReader.newInstance(payload);
				header = reader.getHeader();
				payload.reset();
				sbdhStream = payload;
			} finally {
			}
		} catch (SbdhException e) {
			listener.orElse(ISendListener.NONE).notifySendStepError(SendStep.SBDH, e);
			throw e;
		}

		log.info("Resolved SBDH: " + header);

		listener.orElse(ISendListener.NONE).notifySendStepResult(SendStep.SBDH, header);
		listener.orElse(ISendListener.NONE).notifySendStepStart(SendStep.LOOKUP);

		Endpoint endpoint;
		try {
			long start = System.currentTimeMillis();
			endpoint = lookupService.lookup(header);
			log.info("Resolved endpoint in " + (System.currentTimeMillis() - start) + " ms : " + endpointToString(endpoint));
		} catch (OxalisTransmissionException e) {
			listener.orElse(ISendListener.NONE).notifySendStepError(SendStep.LOOKUP, e);
			throw new TransmissionLookupException(e.getMessage(), e);
		}

		listener.orElse(ISendListener.NONE).notifySendStepResult(SendStep.LOOKUP, endpoint);

		DelisTransmissionRequest r = DelisTransmissionRequest.builder().header(header).endpoint(endpoint).payload(sbdhStream).build();

		return r;
	}

	private String endpointToString(Endpoint endpoint) {
		StringBuilder sb = new StringBuilder();
		if (endpoint != null) {
			sb.append(endpoint.getAddress());
			sb.append(" ");
			sb.append(endpoint.getTransportProfile());
			sb.append(" ");
			if (endpoint.getCertificate() != null) {
				sb.append(endpoint.getCertificate().getSubjectDN());
			}
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
}
