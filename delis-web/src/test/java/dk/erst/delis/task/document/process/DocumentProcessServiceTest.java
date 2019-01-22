package dk.erst.delis.task.document.process;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.common.util.StatData;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class DocumentProcessServiceTest {

	@Autowired
	private DocumentProcessService documentProcessService;
	
	@Test
	public void testProcessLoaded() {
		StatData statData = documentProcessService.processLoaded();
		System.out.println(statData.toStatString());
		assertNotNull(statData);
	}

}
