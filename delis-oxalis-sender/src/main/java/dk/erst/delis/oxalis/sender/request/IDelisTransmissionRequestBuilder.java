package dk.erst.delis.oxalis.sender.request;

import java.io.IOException;
import java.io.InputStream;

import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public interface IDelisTransmissionRequestBuilder {

	DelisTransmissionRequest build(InputStream payload) throws IOException, OxalisTransmissionException, SbdhException;

}
