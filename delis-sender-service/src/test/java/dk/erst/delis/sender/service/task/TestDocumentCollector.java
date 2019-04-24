package dk.erst.delis.sender.service.task;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import dk.erst.delis.sender.service.collector.IDocumentCollector;
import dk.erst.delis.sender.service.document.DocumentData;
import dk.erst.delis.sender.service.document.IDocumentData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
@Getter
public class TestDocumentCollector implements IDocumentCollector {

	private volatile int takeCount = 0;
	private String testResource = "/BIS3_Invoice_TL_TEST.xml";

	@Override
	public IDocumentData findDocument() {
		try {
			this.takeCount++;
			DocumentData d = new DocumentData();
			byte[] bytes;
			try (InputStream is = this.getClass().getResourceAsStream(testResource)) {
				bytes = IOUtils.toByteArray(is);
			}
			d.setData(bytes);
			d.setDescription("Test file " + testResource);
			return d;
		} catch (IOException e) {
			log.error("Failed to read test file " + testResource, e);
		}
		if (takeCount > 100) {
			return null;
		}
		return null;
	}
}
