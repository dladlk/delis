package dk.erst.delis.sender.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.sender.result.EmptyResultProcessor;
import dk.erst.delis.sender.service.task.TestDocumentCollector;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class SenderApplicationTests {

	@Autowired
	private TestDocumentCollector documentCollector;

	@Autowired
	private EmptyResultProcessor emptyResultProcessor;

	@Test
	public void contextLoads() {
		while (emptyResultProcessor.getTotalCount() == 0 && documentCollector.getTakeCount() < 2) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

}
