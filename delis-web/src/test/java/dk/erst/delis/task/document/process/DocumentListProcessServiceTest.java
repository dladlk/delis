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
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DocumentListProcessServiceTest {

	@Autowired
	private DocumentListProcessService documentListProcessService;
	
	@Test
	public void testProcessLoaded() {
		StatData statData = documentListProcessService.processLoaded();
		System.out.println(statData.toStatString());
		assertNotNull(statData);
	}

}
