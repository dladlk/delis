package dk.erst.delis.oxalis.sender.request;

import java.io.InputStream;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import network.oxalis.api.outbound.TransmissionRequest;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;

@Getter
@Builder
@Data
public class DelisTransmissionRequest implements TransmissionRequest, Serializable {

	private static final long serialVersionUID = 5493008704054063660L;

	private final Endpoint endpoint;
	private final Header header;
	private final InputStream payload;

	public boolean isAs4() {
		return this.endpoint.getTransportProfile().getIdentifier().indexOf("-as4-") > 0;
	}
}
