package dk.erst.delis.persistence.stat;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.erst.delis.persistence.stat.StatDao.KeyValue;
import dk.erst.delis.persistence.stat.StatDao.StatRange;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class StatDaoTest {

	@Autowired
	private StatDao statDao;

	@Test
	public void testLoadStat() {
		List<KeyValue> stat;

		for (int h = 0; h < 2; h++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					for (int r = 0; r < 4; r++) {

						int diffHours = h;
						boolean loadHourNotDate = i == 0;
						Long organisationId = j == 0 ? null : 1L;
						StatRange statRange = r == 0 ? new StatRange() : r == 1 ? StatRange.of("2019-01-01", "2019-01-06") : r == 2 ? StatRange.of("2019-01-01", null) : StatRange.of(null, "2019-01-06");

						stat = statDao.loadStat(statRange, loadHourNotDate, diffHours, organisationId);
						assertMy(stat);
					}
				}
			}
		}

	}

	private void assertMy(Object stat) {
		System.out.println(stat);
		assertNotNull(stat);
	}

}
