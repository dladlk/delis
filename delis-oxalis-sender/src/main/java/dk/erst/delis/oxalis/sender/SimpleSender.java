package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.outbound.MessageSender;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.persist.PayloadPersister;
import no.difi.oxalis.api.persist.ReceiptPersister;
import no.difi.oxalis.as4.api.MessageIdGenerator;
import no.difi.oxalis.as4.common.DefaultMessageIdGenerator;
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

	private MemoryPersister memoryPersister = new MemoryPersister();
	private Injector injector;
	private MessageSender messageSenderAs4;
	private MessageSender messageSenderAs2;

	public SimpleSender() {
		injector = getInjector();

		messageSenderAs4 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as4")));
		messageSenderAs2 = injector.getInstance(Key.get(MessageSender.class, Names.named("oxalis-as2")));
	}

	public void sendFile(final byte[] payload, final boolean as4) throws Exception {
		TransmissionRequest tr = new TransmissionRequest() {
			@Override
			public Endpoint getEndpoint() {
				String url;
				TransportProfile transportProfile;
				if (as4) {
					url = "https://peppol-test.trueservice.dk/domibus/services/msh";
					transportProfile = TransportProfile.AS4;
				} else {
					url = "https://peppol-test.trueservice.dk/oxalis/as2";
					transportProfile = TransportProfile.of("busdox-transport-as2-ver1p0");
				}

				return Endpoint.of(transportProfile, URI.create(url), injector.getInstance(X509Certificate.class));
			}

			@Override
			public Header getHeader() {
				return Header.newInstance().sender(ParticipantIdentifier.of("0088:5790001968526")).receiver(ParticipantIdentifier.of("0088:5790001968526"))
						.documentType(DocumentTypeIdentifier
								.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"))
						.process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"));
			}

			@Override
			public InputStream getPayload() {
				return new ByteArrayInputStream(payload);
			}
		};

		long start = System.currentTimeMillis();
		TransmissionResponse response;
		if (as4) {
			response = messageSenderAs4.send(tr);
		} else {
			response = messageSenderAs2.send(tr);
		}
		log.info("Sent to endpoint "+response.getEndpoint().getAddress());
		log.info("Header: "+response.getHeader());
		
		log.debug("Done in " + (System.currentTimeMillis() - start) + " ms");
		memoryPersister.reset();
	}

	public Injector getInjector() {
		return Guice.createInjector(new As4OutboundModule(), new As4InboundModule(), Modules.override(new GuiceModuleLoader()).with(new AbstractModule() {
			@Override
			protected void configure() {
				bind(ReceiptPersister.class).toInstance((m, p) -> {
				});
				bind(PayloadPersister.class).toInstance(memoryPersister);
				bind(MessageIdGenerator.class).toInstance(new DefaultMessageIdGenerator("test.com"));
			}
		}));
	}

	static class MemoryPersister implements PayloadPersister {
		enum Types {
			ID, HEADER, PAYLOAD
		}

		private Map<String, EnumMap<Types, Object>> persistedData = new TreeMap<>();

		@Override
		public Path persist(TransmissionIdentifier transmissionIdentifier, Header header, InputStream is) throws IOException {
			log.debug("Persisting transmission " + transmissionIdentifier);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			IOUtils.copy(is, buffer);

			EnumMap<Types, Object> map = new EnumMap<>(Types.class);
			map.put(Types.ID, transmissionIdentifier);
			map.put(Types.HEADER, header);
			map.put(Types.PAYLOAD, buffer.toByteArray());

			persistedData.put(transmissionIdentifier.getIdentifier(), map);

			return null;
		}

		public void reset() {
			persistedData.clear();
		}

		public Map<String, EnumMap<Types, Object>> getPersistedData() {
			return Collections.unmodifiableMap(persistedData);
		}
	}
}
