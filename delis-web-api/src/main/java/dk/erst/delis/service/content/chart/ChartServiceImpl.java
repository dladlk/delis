package dk.erst.delis.service.content.chart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.erst.delis.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.persistence.stat.StatDao;
import dk.erst.delis.persistence.stat.StatDao.KeyValue;
import dk.erst.delis.persistence.stat.StatDao.StatRange;
import dk.erst.delis.persistence.stat.StatDao.StatType;
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartServiceImpl implements ChartService {

	protected static final String INPUT_NOW_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final DateTimeFormatter OUTPUT_DAILY_FORMAT = DateTimeFormatter.ofPattern("dd.MM");
	private static final DateTimeFormatter OUTPUT_HOURLY_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DB_FORMAT = DateTimeFormatter.ofPattern(INPUT_NOW_FORMAT);

	private StatDao statDao;
	private final SecurityService securityService;

	@Autowired
	public ChartServiceImpl(StatDao statDao, SecurityService securityService) {
		this.statDao = statDao;
		this.securityService = securityService;
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ChartData generateChartData(WebRequest request) {
		String start = request.getParameter("from");
		String end = request.getParameter("to");
		String nowUI = request.getParameter("now");
		return generateChartData(start, end, nowUI);
	}

	@Transactional(readOnly = true)
	protected ChartData generateChartData(String startStr, String endStr, String nowUI) {
		long start = System.currentTimeMillis();

		Long organisationId = loadOrganisationId();
		log.debug("Start chart loading for " + startStr + " - " + endStr + ", orgId " + organisationId+", now "+nowUI);
		
		SimpleDateFormat sdf = new SimpleDateFormat(INPUT_NOW_FORMAT);
		Date uiTimeNow;
		try {
			uiTimeNow = sdf.parse(nowUI);
		} catch (ParseException e) {
			log.error("Failed to parse "+nowUI, e);
			e.printStackTrace();
			uiTimeNow = new Date();
		}
		
		Date dbTimeNow = statDao.loadDbTimeNow();
		int hoursDiff = calculateHoursDiff(uiTimeNow, dbTimeNow);
		if (log.isDebugEnabled()) {
			log.debug("Hours diff between UI and DB: " + hoursDiff);
		}

		StatRange statRange = StatRange.of(startStr, endStr);
		final boolean groupHourNotDate = statRange.isSingleDay();
		log.debug("groupHourNotDate: " + groupHourNotDate);

		final boolean today = statRange.isSingleDay() && nowUI.startsWith(startStr);
		log.debug("today: " + today);
		ChartData chartData = new ChartData();

		uiTimeNow = DateUtil.addHour(uiTimeNow, 1);
		for (StatType statType : StatType.values()) {
			processStatType(statType, organisationId, uiTimeNow, hoursDiff, statRange, groupHourNotDate, today, chartData);
		}

		log.debug("Done chart data in " + (System.currentTimeMillis() - start) + " with: " + chartData);

		return chartData;
	}

	private void processStatType(StatType statType, Long organisationId, Date uiTimeNow, int hoursDiff, StatRange statRange, final boolean groupHourNotDate, final boolean today, ChartData chartData) {
		long start = System.currentTimeMillis();
		List<KeyValue> list = statDao.loadStat(statType, statRange, groupHourNotDate, hoursDiff, organisationId);

		if (log.isDebugEnabled()) {
			log.debug("Loaded stat by "+statType+": " + list+" in "+(System.currentTimeMillis() - start)+" ms");
		}

		boolean noLabels = chartData.getLineChartLabels().isEmpty();
		LineChartData lineChartDataContent = new LineChartData();
		lineChartDataContent.setLabel(statType.getChartLabel());
		List<Long> dataGraph = new ArrayList<>();

		if (!list.isEmpty()) {
			Map<String, Long> mappedList = list.stream().collect(Collectors.toMap(k -> {
				return formatKeyValue(k, groupHourNotDate);
			}, KeyValue::getValue));

			if (groupHourNotDate) {
				int lastChartHour = today ? uiTimeNow.getHours() : 23;
				for (int i = 0; i <= lastChartHour; i++) {
					String outputLabel = (i < 10 ? "0" : "") + i + ":00";
					long outputValue = 0;

					if (mappedList.containsKey(outputLabel)) {
						outputValue = mappedList.get(outputLabel);
					}

					if (noLabels) {
						chartData.addLineChartLabel(outputLabel);
					}
					
					dataGraph.add(outputValue);
				}
			} else {
				LocalDate first = statRange.getFrom() != null ? LocalDate.parse(statRange.getFrom()) : parseKey(list.get(0)).toLocalDate();
				LocalDate last = statRange.getTo() != null ? LocalDate.parse(statRange.getTo()) : parseKey(list.get(list.size() - 1)).toLocalDate();

				LocalDate cur = first;

				while (!cur.isAfter(last)) {
					String outputLabel = cur.format(OUTPUT_DAILY_FORMAT);
					cur = cur.plusDays(1);

					long outputValue = 0;

					if (mappedList.containsKey(outputLabel)) {
						outputValue = mappedList.get(outputLabel);
					}

					if (noLabels) {
						chartData.addLineChartLabel(outputLabel);
					}

					dataGraph.add(outputValue);
				}
			}
		}

		lineChartDataContent.setData(dataGraph);
		chartData.addLineChart(lineChartDataContent);
	}

	protected int calculateHoursDiff(Date uiTimeNow, Date dbTimeNow) {
		ZonedDateTime nowDB = ZonedDateTime.ofInstant(dbTimeNow.toInstant(), ZoneOffset.UTC);
		ZonedDateTime nowUI = ZonedDateTime.ofInstant(uiTimeNow.toInstant(), ZoneOffset.UTC);

		return (int) ChronoUnit.HOURS.between(nowDB, nowUI);
	}

	private static String formatKeyValue(KeyValue k, boolean groupHourNotDate) {
		LocalDateTime dateTime = parseKey(k);
		DateTimeFormatter outputFormat = getFormat(groupHourNotDate);
		return dateTime.format(outputFormat);
	}

	protected static DateTimeFormatter getFormat(boolean groupHourNotDate) {
		return groupHourNotDate ? OUTPUT_HOURLY_FORMAT : OUTPUT_DAILY_FORMAT;
	}

	private static LocalDateTime parseKey(KeyValue k) {
		return LocalDateTime.parse(k.getKey(), DB_FORMAT);
	}

	private Long loadOrganisationId() {
		if (SecurityUtil.hasRole("ROLE_USER")) {
			Organisation organisation = securityService.getOrganisation();
			Long orgId = organisation.getId();
			if (orgId == null) {
				conflictProcess();
			}
			return organisation.getId();
		}
		return null;
	}

	private void conflictProcess() {
		throw new RestConflictException(Collections.singletonList(new FieldErrorModel("organization", HttpStatus.CONFLICT.getReasonPhrase(), "there was a problem reading your organization")));
	}
}
