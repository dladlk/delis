package dk.erst.delis.sender.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.sender.service.task.TestDocumentCollector;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DelisSenderServiceApplicationTests {

	@Autowired
	private TestDocumentCollector documentCollector;

	@Test
	public void contextLoads() {
		while (documentCollector.getTakeCount() < 2) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

}