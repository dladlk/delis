package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import dk.erst.delis.oxalis.sender.ISendListener;
import dk.erst.delis.oxalis.sender.TransmissionLookupException;
import network.oxalis.vefa.peppol.sbdh.lang.SbdhException;

public interface IDelisTransmissionRequestBuilder {

	DelisTransmissionRequest build(InputStream payloadStream, Optional<ISendListener> listener) throws IOException, TransmissionLookupException, SbdhException;

}
