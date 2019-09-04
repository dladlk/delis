package dk.erst.delis.service.content.chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import dk.erst.delis.persistence.stat.StatDao;
import dk.erst.delis.persistence.stat.StatDao.KeyValue;
import dk.erst.delis.persistence.stat.StatDao.StatRange;
import dk.erst.delis.persistence.stat.StatDao.StatType;
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
		List<String> testListStr = new ArrayList<String>();
		for (int i = 0; i < TEST_DATA.length; i++) {
			String factDate = TEST_DATA[i];
			testListStr.add(factDate);
		}

		StatDao statDao = mock(StatDao.class);
		when(statDao.loadDbTimeNow()).thenReturn(calculateDbNow());
		when(statDao.loadFullRange(nullable(Long.class))).then(d -> {
			return dbStatRange;
		});
		when(statDao.loadStat(nullable(StatType.class), nullable(StatRange.class), anyBoolean(), anyInt(), nullable(Long.class))).then(d -> {
			StatType type = (StatType) d.getArgument(0);
			StatRange range = (StatRange) d.getArgument(1);
			boolean groupHourNotDays = (Boolean) d.getArgument(2);

			final String fromStr = range.getFrom() != null ? range.getFrom() : "";
			final String toStr = range.getTo() != null ? range.getTo().substring(0, 10) + "23:59" : "ZZZ";

			List<String> filtered = testListStr.stream().filter(v -> {
				return fromStr.compareTo(v) <= 0 && toStr.compareTo(v) >= 0;
			}).collect(Collectors.toList());

			System.out.println("Filtered for " + type + ": " + filtered);

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
		ChartData r = service.generateChartData(start, end, new SimpleDateFormat(ChartServiceImpl.INPUT_NOW_FORMAT).format(Calendar.getInstance().getTime()));
		for (LineChartData d : r.getLineChartData()) {
			List<Long> data = d.getData();
			assertEquals("Wrong number of blocks for a case " + start + "-" + end, blocks, data.size());
			assertEquals("Wrong number of total result for a case " + start + "-" + end, totalResult, data.stream().mapToInt(Long::intValue).sum());
		}
	}

	@Test
	public void testCalculateHoursDiff() throws ParseException {
		ChartServiceImpl s = new ChartServiceImpl(null, null);

		String[] TEST_HOURS_DIFF = new String[] {

				"2019-01-01 12:00_2019-01-01 09:00_3",

				"2019-01-01 12:00_2019-01-01 15:00_-3",

				"2019-01-01 00:01_2018-12-31 23:01_1",

				"2020-02-29 21:01_2020-03-01 01:01_-4",

		};

		SimpleDateFormat sdf = new SimpleDateFormat(ChartServiceImpl.INPUT_NOW_FORMAT);

		for (String testCase : TEST_HOURS_DIFF) {
			String[] spl = testCase.split("_");
			Date ui = sdf.parse(spl[0] + ":00");
			Date db = sdf.parse(spl[1] + ":00");
			int expected = Integer.parseInt(spl[2]);

			assertEquals("Failed on test case " + testCase, expected, s.calculateHoursDiff(ui, db));
		}

	}

	@Test
	public void testGenerateChartDataDifferentRanges() {
		StatDao statDao = mock(StatDao.class);
		when(statDao.loadDbTimeNow()).thenReturn(calculateDbNow());
		when(statDao.loadFullRange(nullable(Long.class))).then(d -> {
			return dbStatRange;
		});
		when(statDao.loadStat(nullable(StatType.class), nullable(StatRange.class), anyBoolean(), anyInt(), nullable(Long.class))).then(d -> {
			StatType type = (StatType) d.getArgument(0);
			switch (type){
				case RECEIVE_ERROR:
					return buildKeyValueList("15.05","20.05");
				case RECEIVE:
					return buildKeyValueList("12.05","20.05");
				case SEND:
				default:
					return buildKeyValueList("01.05","01.06");
			}
		});

		SecurityService securityService = mock(SecurityService.class);
		when(securityService.getOrganisation()).thenReturn(null);
		service = new ChartServiceImpl(statDao, securityService);

		ChartData chartData = service.generateChartData(null, null, new SimpleDateFormat(ChartServiceImpl.INPUT_NOW_FORMAT).format(Calendar.getInstance().getTime()));
		List<String> labels = chartData.getLineChartLabels();
		assertEquals("01.05.2019", labels.get(0));
		assertEquals("01.06.2019", labels.get(labels.size() - 1));
	}

	private List<KeyValue> buildKeyValueList(String ... s) {
		List<KeyValue> res = new ArrayList<KeyValue>();
		for (int i = 0; i < s.length; i++) {
			String v = s[i];
			String dbFormat = "2019-"+v.substring(3)+"-"+v.substring(0,2)+" 12:00:00";
			KeyValue kv = new KeyValue(dbFormat, 1);
			res.add(kv);
		}
		return res;
	}

}
