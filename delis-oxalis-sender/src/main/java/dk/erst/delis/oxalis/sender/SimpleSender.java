package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import dk.erst.delis.oxalis.sender.request.DelisTransmissionRequest;
import dk.erst.delis.oxalis.sender.request.IDelisTransmissionRequestBuilder;
import dk.erst.delis.oxalis.sender.response.DelisResponse;
import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.outbound.MessageSender;
import no.difi.oxalis.api.persist.PersisterHandler;

@Slf4j
public class SimpleSender {

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

	public void sendFile(final byte[] payload) throws Exception {
		DelisTransmissionRequest tr = this.requestBuilder.build(new ByteArrayInputStream(payload));

		long start = System.currentTimeMillis();
		DelisResponse response;
		if (tr.isAs4()) {
			response = DelisResponse.of(messageSenderAs4.send(tr));
		} else {
			response = DelisResponse.of(messageSenderAs2.send(tr));
		}

		// Persist receipt - default persister does not save payload
		persisterHandler.persist(response, null);
		// Persist sent payload
		persisterHandler.persist(response.getTransmissionIdentifier(), response.getHeader(), new ByteArrayInputStream(payload));

		log.info("Sent to endpoint " + response.getEndpoint().getAddress());
		log.info("Header: " + response.getHeader());

		log.debug("Done in " + (System.currentTimeMillis() - start) + " ms");
	}

}
