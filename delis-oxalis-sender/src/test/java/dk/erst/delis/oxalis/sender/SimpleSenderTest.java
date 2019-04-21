package dk.erst.delis.oxalis.sender;

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
import no.difi.oxalis.api.lookup.LookupService;
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
public class SimpleSenderTest {

	private static Injector injector;
	private static LookupTransmissionRequestBuilder lookupBuilder;
	private static StaticTransmissionRequestBuilder staticBuilder;
	private static X509Certificate certificate;

	@BeforeClass
	public static void init() {
		injector = Guice
				.createInjector(new As4OutboundModule(), new As4InboundModule(), Modules
						.override(new GuiceModuleLoader())
							.with(new AbstractModule() {
							}));

		staticBuilder = new StaticTransmissionRequestBuilder();
		certificate = injector.getInstance(X509Certificate.class);

		LookupService lookupService = injector.getInstance(LookupService.class);
		lookupBuilder = new LookupTransmissionRequestBuilder(lookupService);
	}

	@Test
	public void testSendFileStatic() throws Exception {
		byte[] payload = loadTestPayload();
		SimpleSender sender = new SimpleSender(injector, staticBuilder);
		sendBothTransports(sender, payload);
	}

	private void sendBothTransports(SimpleSender sender, byte[] payload) throws Exception {
		staticBuilder.setHeader(buildHeader());

		log.info("Sending by AS2...");
		staticBuilder.setEndpoint(buildEndpoint(false));
		sendPayload(sender, payload);

		log.info("Sending by AS4...");
		staticBuilder.setEndpoint(buildEndpoint(true));
		sendPayload(sender, payload);
	}

	private void sendPayload(SimpleSender sender, byte[] payload) throws Exception {
		long start = System.currentTimeMillis();

		DelisResponse response = sender.send(payload);

		log.info("Sent to endpoint " + response.getEndpoint().getAddress());
		log.info("Header: " + response.getHeader());

		log.debug("Done in " + (System.currentTimeMillis() - start) + " ms");
	}

	private Header buildHeader() {
		String sender = "0088:5798009882875";
		String receiver = sender;
		String documentIdentifier = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1";
		String processIdentifier = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

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
			url = "https://peppol-test.trueservice.dk/domibus/services/msh";
			transportProfile = TransportProfile.AS4;
		} else {
			url = "https://peppol-test.trueservice.dk/oxalis/as2";
			transportProfile = TransportProfile.of("busdox-transport-as2-ver1p0");
		}

		return Endpoint.of(transportProfile, URI.create(url), certificate);
	}

	@Test
	public void testSendFileByLookup() throws Exception {
		byte[] payload = loadTestPayload();

		SimpleSender s = new SimpleSender(injector, lookupBuilder);
		log.info("Sending by dynamic lookup...");
		sendPayload(s, payload);
	}

	@Test
	@Ignore
	public void testSendFileStaticMany() throws Exception {
		byte[] payload = loadTestPayload();
		SimpleSender sender = new SimpleSender(injector, staticBuilder);

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
