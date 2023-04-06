package dk.erst.delis.sender.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import dk.erst.delis.oxalis.sender.ISender;
import dk.erst.delis.oxalis.sender.SimpleSender;
import dk.erst.delis.oxalis.sender.request.LookupTransmissionRequestBuilder;
import dk.erst.delis.sender.collector.IDocumentCollector;
import dk.erst.delis.sender.result.IResultProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.api.lookup.LookupService;
import network.oxalis.commons.guice.GuiceModuleLoader;

@Component
@Slf4j
@Getter
@Setter
public class SendContext {

	private ISender sender;

	@Autowired
	private IDocumentCollector documentCollector;
	
	@Autowired
	private IResultProcessor resultProcessor;

	private Injector injector;

	@PostConstruct
	public void init() {
		long start = System.currentTimeMillis();
		
		log.info("Start Guice initialization of " + this.getClass().getSimpleName() + "...");
		injector = Guice.createInjector(Modules.override(new GuiceModuleLoader()).with(new AbstractModule() {
		}));

		LookupService lookupService = injector.getInstance(LookupService.class);
		LookupTransmissionRequestBuilder transmissionRequestBuilder = new LookupTransmissionRequestBuilder(lookupService);

		sender = new SimpleSender(injector, transmissionRequestBuilder);

		log.info(this.getClass().getSimpleName() + " initialized in " + (System.currentTimeMillis() - start) + " ms");
	}

}
