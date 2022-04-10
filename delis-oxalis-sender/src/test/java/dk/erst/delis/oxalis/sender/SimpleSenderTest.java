package dk.erst.delis.oxalis.sender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import dk.erst.delis.oxalis.sender.request.LookupTransmissionRequestBuilder;
import dk.erst.delis.oxalis.sender.request.StaticTransmissionRequestBuilder;
import dk.erst.delis.oxalis.sender.response.DelisResponse;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.api.lookup.LookupService;
import network.oxalis.commons.guice.GuiceModuleLoader;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import network.oxalis.vefa.peppol.mode.Mode;

@Slf4j
public class SimpleSenderTest {

	private static final boolean SEND_AS2 = false;
	
	private static final boolean SEND_TO_TL_ACCEPTANCE = false;
	
	private static Injector injector;
	private static LookupTransmissionRequestBuilder lookupBuilder;
	private static StaticTransmissionRequestBuilder staticBuilder;
	private static X509Certificate certificate;

	@BeforeClass
	public static void init() {
		injector = Guice.createInjector(Modules.override(new GuiceModuleLoader()).with(new AbstractModule() {
		}));

		staticBuilder = new StaticTransmissionRequestBuilder();
		certificate = injector.getInstance(X509Certificate.class);

		LookupService lookupService = injector.getInstance(LookupService.class);
		lookupBuilder = new LookupTransmissionRequestBuilder(lookupService);
	}

	@Test
	public void testSendFileStatic() throws Exception {
		byte[] payload = loadTestPayload();
		ISender sender = new SimpleSender(injector, staticBuilder);
		sendBothTransports(sender, payload);
	}

	private void sendBothTransports(ISender sender, byte[] payload) throws Exception {
		staticBuilder.setHeader(buildHeader());
		if (SEND_AS2) {
			log.info("Sending by AS2...");
			staticBuilder.setEndpoint(buildEndpoint(false));
			sendPayload(sender, payload);
		}

		log.info("Sending by AS4...");
		staticBuilder.setEndpoint(buildEndpoint(true));
		sendPayload(sender, payload);
	}

	private void sendPayload(ISender sender, byte[] payload) throws Exception {
		long start = System.currentTimeMillis();

		DelisResponse response = sender.send(new ByteArrayInputStream(payload));

		log.info("Sent to endpoint " + response.getEndpoint().getAddress());
		log.info("Header: " + response.getHeader());

		log.debug("Done in " + (System.currentTimeMillis() - start) + " ms");
	}

	private Header buildHeader() {
		String sender = "0088:5798009882875";
		String receiver = sender;
		String documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1";
		String processIdentifier = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

		if (SEND_TO_TL_ACCEPTANCE) {
			documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1";
			processIdentifier = "urn:www.cenbii.eu:profile:bii05:ver2.0";
		}

		Header header = Header
				.newInstance()
					.sender(ParticipantIdentifier.of(sender))
					.receiver(ParticipantIdentifier.of(receiver))
					.documentType(DocumentTypeIdentifier.of(documentIdentifier))
					.process(ProcessIdentifier.of(processIdentifier));
		return header;
	}

	private Endpoint buildEndpoint(boolean as4) {
		String url;
		TransportProfile transportProfile;
		if (as4) {
			transportProfile = TransportProfile.AS4;
		} else {
			transportProfile = TransportProfile.of("busdox-transport-as2-ver1p0");
		}
		url = Mode.of(Mode.TEST).getString("oxalis.dev.url."+(as4?"as4":"as2"));
		if (SEND_TO_TL_ACCEPTANCE) {
			url = "https://peppol-test.trueservice.dk/domibus/services/msh";
		}
		
		return Endpoint.of(transportProfile, URI.create(url), certificate);
	}

	@Test
	@Ignore
	public void testSendFileByLookup() throws Exception {
		byte[] payload = loadTestPayload();

		ISender s = new SimpleSender(injector, lookupBuilder);
		log.info("Sending by dynamic lookup...");
		sendPayload(s, payload);
	}

	@Test
	@Ignore
	public void testSendFileStaticMany() throws Exception {
		byte[] payload = loadTestPayload();
		ISender sender = new SimpleSender(injector, staticBuilder);

		for (int i = 0; i < 10; i++) {
			this.sendBothTransports(sender, payload);
		}
	}

	public static byte[] loadTestPayload() throws IOException {
		InputStream is = SimpleSenderTest.class.getResourceAsStream("/tl-test-sbdh.xml");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		IOUtils.copy(is, buffer);

		byte[] payload = buffer.toByteArray();
		return payload;
	}

}
