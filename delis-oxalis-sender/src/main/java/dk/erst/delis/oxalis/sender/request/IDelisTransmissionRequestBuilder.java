package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public interface IDelisTransmissionRequestBuilder {

	DelisTransmissionRequest build(InputStream payload) throws IOException, TransmissionLookupException, SbdhException;

}
