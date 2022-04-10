package dk.erst.delis.sender.service.lookup;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.locator.AbstractLocator;
import network.oxalis.vefa.peppol.mode.Mode;

@Slf4j
public class SingleSMPLocator extends AbstractLocator {

	private String smpUrl;

	public SingleSMPLocator(Mode mode) {
		this(mode.getString("lookup.locator.smp.url"));
	}

	public SingleSMPLocator(String hostname) {
		this.smpUrl = hostname;
	}

	@Override
	public URI lookup(ParticipantIdentifier participantIdentifier) throws LookupException {
        if (log.isDebugEnabled()) {
        	log.debug("Lookup URL is fixed to single SMP: "+smpUrl);
        }
        return URI.create(smpUrl);
	}

	
	
}
