package dk.erst.delis.oxalis.sender.request;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import dk.erst.delis.oxalis.sender.ISendListener;
import dk.erst.delis.oxalis.sender.SimpleSenderTest;
import network.oxalis.api.lookup.LookupService;
import network.oxalis.as4.inbound.As4InboundModule;
import network.oxalis.as4.outbound.As4OutboundModule;
import network.oxalis.commons.guice.GuiceModuleLoader;

@Ignore
public class LookupTransmissionRequestBuilderTest {

	@Test
	public void testBuild() throws Exception {
		Injector injector = Guice
				.createInjector(new As4OutboundModule(), new As4InboundModule(), Modules
						.override(new GuiceModuleLoader())
							.with(new AbstractModule() {
							}));

		LookupService lookupService = injector.getInstance(LookupService.class);
		IDelisTransmissionRequestBuilder b = new LookupTransmissionRequestBuilder(lookupService);

		byte[] testXmlSbdh = SimpleSenderTest.loadTestPayload();

		DelisTransmissionRequest r = b.build(new ByteArrayInputStream(testXmlSbdh), Optional.<ISendListener>empty());
		assertNotNull(r.getHeader());
		assertNotNull(r.getEndpoint());
		assertNotNull(r.getPayload());
	}

}
