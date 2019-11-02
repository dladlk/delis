package dk.erst.delis.web.error;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class ErrorDictionaryStatRepositoryTest {

	@Autowired
	private ErrorDictionaryStatRepository errorDictionaryStatRepository;

	@Test
	public void testFindErrorStatByErrorId() {
		errorDictionaryStatRepository.findErrorStatByErrorId(0L);
	}

	@Test
	public void loadErrorStatBySenderCountry() {
		errorDictionaryStatRepository.loadErrorStatBySenderCountry(0L);
		errorDictionaryStatRepository.loadErrorStatBySenderName(0L);
	}

}
