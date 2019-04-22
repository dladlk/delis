package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public interface ISender {

	DelisResponse send(byte[] payload) throws Exception;

	DelisResponse send(ByteArrayInputStream payloadStream) throws IOException, OxalisTransmissionException, SbdhException;

}