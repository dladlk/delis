package dk.erst.delis.persistence.stat;

import static org.junit.Assert.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
		LocalDateTime from = LocalDate.now().atStartOfDay();

		Date today = Date.valueOf(from.toLocalDate());
		Date tomorrow = Date.valueOf(LocalDate.now().plusDays(1).atStartOfDay().toLocalDate());

		List<KeyValue> stat;

		for (int h = 0; h < 2; h++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					int diffHours = h;
					boolean loadHourNotDate = i == 0;
					Long organisationId = j == 0 ? null : 1L;

					StatRange fullRange = statDao.loadFullRange(organisationId);
					assertMy(fullRange);

					stat = statDao.loadStat(fullRange, loadHourNotDate, diffHours, organisationId);
					assertMy(stat);

					stat = statDao.loadStat(StatRange.of(today, tomorrow), loadHourNotDate, diffHours, organisationId);
					assertMy(stat);
				}
			}
		}

	}

	private void assertMy(Object stat) {
		System.out.println(stat);
		assertNotNull(stat);
	}

}
