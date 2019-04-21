package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import dk.erst.delis.document.sbdh.AlreadySBDHException;
import dk.erst.delis.document.sbdh.SBDHTranslator;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.lookup.LookupService;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public class LookupTransmissionRequestBuilder implements IDelisTransmissionRequestBuilder {

	private LookupService lookupService;

	public LookupTransmissionRequestBuilder(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public DelisTransmissionRequest build(InputStream payload) throws IOException, OxalisTransmissionException, SbdhException {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		SBDHTranslator t = new SBDHTranslator();
		Header header;
		try {
			header = t.addHeader(payload, out);
		} catch (AlreadySBDHException e) {
			payload.reset();
			SbdReader reader = SbdReader.newInstance(payload);
			header = reader.getHeader();
		}

		Endpoint endpoint = lookupService.lookup(header);

		DelisTransmissionRequest r = DelisTransmissionRequest
				.builder()
					.header(header)
					.endpoint(endpoint)
					.payload(payload)
					.build();

		return r;
	}
}
