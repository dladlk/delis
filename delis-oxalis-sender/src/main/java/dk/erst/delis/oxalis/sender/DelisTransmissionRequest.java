package dk.erst.delis.oxalis.sender;

import java.io.InputStream;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;

@Getter
@Builder
@Data
public class DelisTransmissionRequest implements TransmissionRequest, Serializable {

	private static final long serialVersionUID = 5493008704054063660L;

	private boolean as4;
	private final Endpoint endpoint;
    private final Header header;
    private final InputStream payload;

}
