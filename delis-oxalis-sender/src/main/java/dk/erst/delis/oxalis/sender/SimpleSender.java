package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import dk.erst.delis.oxalis.sender.request.DelisTransmissionRequest;
import dk.erst.delis.oxalis.sender.request.IDelisTransmissionRequestBuilder;
import dk.erst.delis.oxalis.sender.response.DelisResponse;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.outbound.MessageSender;
import no.difi.oxalis.api.persist.PersisterHandler;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;

public class SimpleSender implements ISender {

	private MessageSender messageSenderAs4;
	private MessageSender messageSenderAs2;
	private IDelisTransmissionRequestBuilder requestBuilder;
	private PersisterHandler persisterHandler;

	public SimpleSender(Injector injector, IDelisTransmissionRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		this.messageSenderAs4 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as4")));
		this.messageSenderAs2 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as2")));
		this.persisterHandler = injector.getInstance(Key.get(PersisterHandler.class, Names.named("default")));
	}

	public DelisResponse send(final byte[] payload) throws Exception {
		ByteArrayInputStream payloadStream = new ByteArrayInputStream(payload);
		return send(payloadStream);
	}

	@Override
	public DelisResponse send(InputStream payloadStream) throws TransmissionLookupException, TransmissionException, SbdhException, IOException {
		return send(payloadStream, Optional.empty());
	}
	
	@Override
	public DelisResponse send(InputStream payloadStream, Optional<ISendListener> listener) throws TransmissionLookupException, TransmissionException, SbdhException, IOException {
		DelisTransmissionRequest tr = this.requestBuilder.build(payloadStream, listener);

		DelisResponse response;

		try {
			if (tr.isAs4()) {
				response = DelisResponse.of(messageSenderAs4.send(tr));
			} else {
				response = DelisResponse.of(messageSenderAs2.send(tr));
			}
		} catch (OxalisTransmissionException e) {
			throw new TransmissionException(e.getMessage(), e);
		}

		// Persist receipt - default persister does not save payload
		persisterHandler.persist(response, null);
		// Persist sent payload
		persisterHandler.persist(response.getTransmissionIdentifier(), response.getHeader(), payloadStream);

		return response;
	}

}
