package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.cert.X509Certificate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

import dk.erst.delis.oxalis.sender.DelisTransmissionRequest.DelisTransmissionRequestBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.outbound.MessageSender;
import no.difi.oxalis.api.persist.PersisterHandler;
import no.difi.oxalis.as4.inbound.As4InboundModule;
import no.difi.oxalis.as4.outbound.As4OutboundModule;
import no.difi.oxalis.commons.guice.GuiceModuleLoader;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.TransportProfile;

@Slf4j
public class SimpleSender {

	private Injector injector;
	private MessageSender messageSenderAs4;
	private MessageSender messageSenderAs2;

	@Setter
	private boolean preferAS2;

	public SimpleSender() {
		injector = Guice
				.createInjector(new As4OutboundModule(), new As4InboundModule(), Modules
						.override(new GuiceModuleLoader())
							.with(new AbstractModule() {
							}));

		messageSenderAs4 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as4")));
		messageSenderAs2 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as2")));
	}

	public void sendFile(final byte[] payload) throws Exception {
		DelisTransmissionRequest tr = buildTransmissionRequest(payload, !this.preferAS2);

		PersisterHandler persisterHandler = injector.getInstance(Key.get(PersisterHandler.class, Names.named("default")));

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

	private DelisTransmissionRequest buildTransmissionRequest(final byte[] payload, final boolean as4) {
		DelisTransmissionRequestBuilder builder = DelisTransmissionRequest.builder();

		String url;
		TransportProfile transportProfile;
		if (as4) {
			url = "https://peppol-test.trueservice.dk/domibus/services/msh";
			transportProfile = TransportProfile.AS4;
		} else {
			url = "https://peppol-test.trueservice.dk/oxalis/as2";
			transportProfile = TransportProfile.of("busdox-transport-as2-ver1p0");
		}

		Endpoint endpoint = Endpoint.of(transportProfile, URI.create(url), injector.getInstance(X509Certificate.class));

		String sender = "0088:5790001968526";
		String receiver = sender;
		String documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1";
		String processIdentifier = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

		Header header = Header
				.newInstance()
					.sender(ParticipantIdentifier.of(sender))
					.receiver(ParticipantIdentifier.of(receiver))
					.documentType(DocumentTypeIdentifier.of(documentIdentifier))
					.process(ProcessIdentifier.of(processIdentifier));

		builder
				.as4(as4)
					.endpoint(endpoint)
					.header(header)
					.payload(new ByteArrayInputStream(payload));

		return builder.build();
	}

}
