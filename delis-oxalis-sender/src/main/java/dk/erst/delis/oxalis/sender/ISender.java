package dk.erst.delis.oxalis.sender;

import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public interface ISender {

	DelisResponse send(byte[] payload) throws Exception;

	DelisResponse send(InputStream payloadStream) throws IOException, OxalisTransmissionException, SbdhException;

}