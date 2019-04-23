package dk.erst.delis.oxalis.sender.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.document.sbdh.AlreadySBDHException;
import dk.erst.delis.document.sbdh.SBDHTranslator;
import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.lookup.LookupService;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

@Slf4j
public class LookupTransmissionRequestBuilder implements IDelisTransmissionRequestBuilder {

	private LookupService lookupService;

	public LookupTransmissionRequestBuilder(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public DelisTransmissionRequest build(InputStream payload) throws IOException, TransmissionLookupException, SbdhException {
		SBDHTranslator t = new SBDHTranslator();
		Header header;
		payload.mark(Integer.MAX_VALUE);
		InputStream sbdhStream;
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

		log.info("Resolved SBDH: " + header);

		Endpoint endpoint;
		try {
			long start = System.currentTimeMillis();
			endpoint = lookupService.lookup(header);
			log.info("Resolved endpoint in " + (System.currentTimeMillis() - start) + " ms : " + endpointToString(endpoint));
		} catch (OxalisTransmissionException e) {
			throw new TransmissionLookupException(e.getMessage(), e);
		}

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
