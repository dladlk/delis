package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.oxalis.sender.request.DelisTransmissionRequest.DelisTransmissionRequestBuilder;
import lombok.Getter;
import lombok.Setter;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

@Getter
@Setter
public class StaticTransmissionRequestBuilder implements IDelisTransmissionRequestBuilder {

	private Header header;
	private Endpoint endpoint;

	public StaticTransmissionRequestBuilder() {
	}

	@Override
	public DelisTransmissionRequest build(InputStream payload) throws IOException, OxalisTransmissionException, SbdhException {
		return buildTransmissionRequest(payload);
	}

	private DelisTransmissionRequest buildTransmissionRequest(final InputStream payload) {
		DelisTransmissionRequestBuilder builder = DelisTransmissionRequest.builder();

		builder
				.endpoint(endpoint)
					.header(header)
					.payload(payload);

		return builder.build();
	}

}
