package dk.erst.peppol.oxalis.sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import dk.erst.delis.oxalis.sender.SimpleSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleSenderTest {

	@Test
	public void testSendFileOne() throws Exception {
		try {
			byte[] payload = loadTestPayload();
			SimpleSender s = new SimpleSender();
			log.info("Sending by AS2...");
			s.sendFile(payload, false);
			
			log.info("Sending by AS4...");
			s.sendFile(payload, true);
		} catch (Exception e) {
			log.error("Failed", e);
			throw e;
		}
	}

	@Test
	@Ignore
	public void testSendFileMany() throws Exception {
		byte[] payload = loadTestPayload();
		SimpleSender s = new SimpleSender();
		for (int i = 0; i < 10; i++) {
			s.sendFile(payload, false);
			s.sendFile(payload, true);
		}
	}

	private byte[] loadTestPayload() throws IOException {
		InputStream is = getClass().getResourceAsStream("/tl-test-sbdh.xml");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		IOUtils.copy(is, buffer);

		byte[] payload = buffer.toByteArray();
		return payload;
	}

}
