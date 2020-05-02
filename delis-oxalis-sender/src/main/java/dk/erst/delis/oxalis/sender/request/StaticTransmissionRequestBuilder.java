package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import dk.erst.delis.oxalis.sender.ISendListener;
import dk.erst.delis.oxalis.sender.SendStep;
import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import dk.erst.delis.oxalis.sender.request.DelisTransmissionRequest.DelisTransmissionRequestBuilder;
import lombok.Getter;
import lombok.Setter;
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
	public DelisTransmissionRequest build(InputStream payload, Optional<ISendListener> listener) throws IOException, TransmissionLookupException, SbdhException {
		listener.orElse(ISendListener.NONE).notifySendStepResult(SendStep.SBDH, header);
		listener.orElse(ISendListener.NONE).notifySendStepResult(SendStep.LOOKUP, endpoint);
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
