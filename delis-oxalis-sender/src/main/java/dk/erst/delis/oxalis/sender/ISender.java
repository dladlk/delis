package dk.erst.delis.oxalis.sender;

import java.io.IOException;
import java.io.InputStream;

import dk.erst.delis.oxalis.sender.response.DelisResponse;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

/**
 * TransmissionLookupException - failed to lookup for receiver
 * 
 * TransmissionException - thrown if something is wrong with delivery to receiver
 * 
 * SbdhException - something is wrong with generated SBDH
 * 
 * IOException - something is really wrong with IO
 */

public interface ISender {

	DelisResponse send(InputStream payloadStream) throws TransmissionLookupException, TransmissionException, SbdhException, IOException;

}