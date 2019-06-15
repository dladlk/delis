package dk.erst.delis.oxalis.sender.request;

import static org.junit.Assert.assertNotNull;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.util.Modules;

import lombok.extern.slf4j.Slf4j;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.lookup.LookupService;
import no.difi.oxalis.as4.inbound.As4InboundModule;
import no.difi.oxalis.as4.outbound.As4OutboundModule;
import no.difi.oxalis.commons.guice.GuiceModuleLoader;
import no.difi.vefa.peppol.common.lang.PeppolLoadingException;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Endpoint;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

@Slf4j
public class DynamicTransmissionRequestBuilderTest {

	@Test
	public void testBuild() throws PeppolLoadingException, OxalisTransmissionException {
		final MethodInterceptor logMethods = new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				long start = System.nanoTime();
				Object next = invocation.proceed();
				log.error("Done " + invocation + " in " + (System.currentTimeMillis() - start));
				return next;
			}
			
		};
		
		final Package matchPackage = LookupService.class.getPackage();
		assertNotNull(matchPackage);
		final SimpleTraceInterceptor tracer = new SimpleTraceInterceptor();
		
		Injector injector = Guice.createInjector(Modules.override(new GuiceModuleLoader()).with(new AbstractModule() {
			@Override
			protected void configure() {
				super.configure();
				this.requestInjection(logMethods);
				this.requestInjection(tracer);
				this.bindInterceptor(Matchers.inPackage(matchPackage), Matchers.any(), logMethods, tracer);
			}
		}), new As4OutboundModule() {

			@Override
			protected void configure() {
				super.configure();
				this.requestInjection(logMethods);
				this.requestInjection(tracer);
				this.bindInterceptor(Matchers.inPackage(matchPackage), Matchers.any(), logMethods, new SimpleTraceInterceptor());
			}
			
		}, new As4InboundModule() {
			
			@Override
			protected void configureServlets() {
				// TODO Auto-generated method stub
				super.configureServlets();
				this.requestInjection(logMethods);
				this.requestInjection(tracer);
				this.bindInterceptor(Matchers.inPackage(matchPackage), Matchers.any(), logMethods, tracer);
			}
			
		});

		LookupService lookupService = injector.getInstance(LookupService.class);
		Header header = new Header().receiver(ParticipantIdentifier.of("0088:5798009882875")).documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1")).process(ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"));

		System.out.println(header);

		Endpoint lookup = lookupService.lookup(header);
		System.out.println(lookup.getAddress());
		// LookupClient lookupClient = LookupClientBuilder.forMode(Mode.TEST).certificateValidator(CertificateValidator.EMPTY).build();

	}

}
