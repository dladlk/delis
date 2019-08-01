package dk.erst.delis.service.content.chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import dk.erst.delis.persistence.stat.StatDao;
import dk.erst.delis.persistence.stat.StatDao.KeyValue;
import dk.erst.delis.persistence.stat.StatDao.StatRange;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.service.security.SecurityService;

public class ChartServiceImplTest {

	private ChartServiceImpl service;

	private int dbDiffHours = 0;
	private StatRange dbStatRange = null;

	private static String TEST_DATA[] = new String[] { "2019-01-01 12:00", "2019-01-01 15:00", "2019-01-01 15:30", "2019-01-02 12:00", "2019-01-03 12:00" };

	@Test
	public void testGenerateChartDataStringString() {
		String pattern = "yyyy-MM-dd HH:mm";

		List<String> testListStr = new ArrayList<String>();
		for (int i = 0; i < TEST_DATA.length; i++) {
			String factDate = TEST_DATA[i];
			testListStr.add(factDate);
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		StatDao statDao = mock(StatDao.class);
		when(statDao.loadDbTimeNow()).thenReturn(calculateDbNow());
		when(statDao.loadFullRange(nullable(Long.class))).then(d -> {
			return dbStatRange;
		});
		when(statDao.loadStat(nullable(StatRange.class), anyBoolean(), anyInt(), nullable(Long.class))).then(d -> {
			StatRange range = (StatRange) d.getArgument(0);
			boolean groupHourNotDays = (Boolean) d.getArgument(1);

			final String fromStr = range.getFrom() != null ? sdf.format(range.getFrom()) : "";
			final String toStr = range.getTo() != null ? sdf.format(range.getTo()).substring(0, 10) + "23:59" : "ZZZ";

			List<String> filtered = testListStr.stream().filter(v -> {
				return fromStr.compareTo(v) <= 0 && toStr.compareTo(v) >= 0;
			}).collect(Collectors.toList());

			System.out.println("Filtered: " + filtered);

			List<String> transformed = filtered.stream().map(v -> {
				if (groupHourNotDays) {
					return v.substring(0, 13) + ":00:00";
				}
				return v.substring(0, 10) + " 00:00:00";
			}).collect(Collectors.toList());

			System.out.println("Transformed: " + transformed);

			List<KeyValue> list = new ArrayList<KeyValue>();

			Map<String, List<String>> grouped = transformed.stream().collect(Collectors.groupingBy(s -> s));
			ArrayList<String> resKeys = new ArrayList<String>(grouped.keySet());
			Collections.sort(resKeys);
			for (String key : resKeys) {
				KeyValue kv = new KeyValue(key, grouped.get(key).size());
				list.add(kv);
			}

			System.out.println("Result: " + list);

			return list;
		});

		SecurityService securityService = mock(SecurityService.class);
		when(securityService.getOrganisation()).thenReturn(null);

		service = new ChartServiceImpl(statDao, securityService);

		assertChartData(5, 3, null, null);
		assertChartData(5, 3, "", "");
		assertChartData(5, 3, "2019-01-01", "");
		assertChartData(4, 2, "2019-01-01", "2019-01-02");
		assertChartData(3, 24, "2019-01-01", "2019-01-01");
		assertChartData(1, 2, "2019-01-03", "2019-01-04");
	}

	private Date calculateDbNow() {
		ZonedDateTime now = ZonedDateTime.now();
		now = now.minusHours(dbDiffHours);
		return Date.from(now.toInstant());
	}

	private void assertChartData(int totalResult, int blocks, String start, String end) {
		ChartData r = service.generateChartData(start, end);
		for (LineChartData d : r.getLineChartData()) {
			List<Long> data = d.getData();
			assertEquals("Wrong number of blocks for a case " + start + "-" + end, blocks, data.size());
			assertEquals("Wrong number of total result for a case " + start + "-" + end, totalResult, data.stream().mapToInt(Long::intValue).sum());
		}
	}

}
