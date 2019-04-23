package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import dk.erst.delis.oxalis.sender.request.DelisTransmissionRequest.DelisTransmissionRequestBuilder;
import lombok.Getter;
import lombok.Setter;
import no.difi.oxalis.api.lookup.LookupService;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

@Getter
@Setter
public class DynamicTransmissionRequestBuilder implements IDelisTransmissionRequestBuilder {

	private LookupService lookupService;

	public DynamicTransmissionRequestBuilder(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Override
	public DelisTransmissionRequest build(InputStream payload) throws IOException, TransmissionLookupException, SbdhException {
		return buildTransmissionRequest(payload);
	}

	private DelisTransmissionRequest buildTransmissionRequest(final InputStream payload) {
		DelisTransmissionRequestBuilder builder = DelisTransmissionRequest.builder();


		return builder.build();
	}

}
