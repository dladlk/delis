package dk.erst.delis.service.content.chart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import org.apache.commons.lang3.StringUtils;
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
import dk.erst.delis.rest.data.response.chart.ChartData;
import dk.erst.delis.rest.data.response.chart.LineChartData;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartServiceImpl implements ChartService {

	private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter OUTPUT_DAILY_FORMAT = DateTimeFormatter.ofPattern("dd.MM");
	private static final DateTimeFormatter OUTPUT_HOURLY_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final ZoneId UI_ZONE = ZoneId.of("Europe/Copenhagen");
	private static final ZoneId DB_ZONE = ZoneOffset.UTC;

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
		return generateChartData(start, end);
	}

	@Transactional(readOnly = true)
	protected ChartData generateChartData(String startStr, String endStr) {
		long start = System.currentTimeMillis();

		Long organisationId = loadOrganisationId();
		log.debug("Start chart loading for " + startStr + " - " + endStr + ", orgId " + organisationId);
		/*
		 * We assume, that input dates are defined in DK time zone - Europe/Copenhagen.
		 * 
		 * But DB time is expressed in UTC - so we should convert it there.
		 */
		/*
		 * See https://stackoverflow.com/a/56508200/11862370 for an overview of LocalDateTime vs ZonedDateTime
		 */
		ZonedDateTime startDate = null;
		if (!StringUtils.isEmpty(startStr)) {
			startDate = LocalDate.parse(startStr, INPUT_DATE_FORMAT).atTime(0, 0).atZone(UI_ZONE);
		}

		ZonedDateTime endDate = null;
		if (!StringUtils.isEmpty(endStr)) {
			endDate = LocalDate.parse(endStr, INPUT_DATE_FORMAT).atTime(0, 0).atZone(UI_ZONE);
		}

		final boolean groupHourNotDate = !StringUtils.isEmpty(startStr) && !StringUtils.isEmpty(endStr) && startStr.equals(endStr);
		log.debug("groupHourNotDate: " + groupHourNotDate);

		final boolean today = groupHourNotDate && startStr.equals(ZonedDateTime.now(UI_ZONE).format(INPUT_DATE_FORMAT));
		log.debug("today: " + today);

		if (startDate == null || endDate == null) {
			/*
			 * Loaded in UTC
			 */
			StatRange fullRange = statDao.loadFullRange(organisationId);
			log.debug("Loaded full range " + fullRange);
			if (fullRange != null) {
				if (startDate == null) {
					startDate = ZonedDateTime.ofInstant(fullRange.getFrom().toInstant(), DB_ZONE);
				}
				if (endDate == null) {
					endDate = ZonedDateTime.ofInstant(fullRange.getTo().toInstant(), DB_ZONE);
				}
			}
		}

		Date loadDbTimeNow = statDao.loadDbTimeNow();
		ZonedDateTime nowDB = ZonedDateTime.ofInstant(loadDbTimeNow.toInstant(), DB_ZONE).withZoneSameLocal(UI_ZONE);
		ZonedDateTime nowUI = ZonedDateTime.now(UI_ZONE);

		int hoursDiff = (int) ChronoUnit.HOURS.between(nowDB, nowUI);

		if (log.isDebugEnabled()) {
			log.debug("Hours diff between UI and DB: " + hoursDiff);
		}

		StatRange statRange = StatRange.of(startDate, endDate);
		List<KeyValue> list = statDao.loadStat(statRange, groupHourNotDate, hoursDiff, organisationId);

		if (log.isDebugEnabled()) {
			log.debug("Loaded stat: " + list);
		}

		ChartData chartData = new ChartData();
		List<LineChartData> lineChartData = new ArrayList<>();
		List<String> lineChartLabels = new ArrayList<>();
		LineChartData lineChartDataContent = new LineChartData();
		lineChartDataContent.setLabel("chart.receiving");
		List<Long> dataGraph = new ArrayList<>();

		if (!list.isEmpty()) {
			Map<String, Long> mappedList = list.stream().collect(Collectors.toMap(k -> {
				return formatKeyValue(k, groupHourNotDate);
			}, KeyValue::getValue));

			if (groupHourNotDate) {
				int lastChartHour = today ? nowUI.getHour() : 23;
				for (int i = 0; i <= lastChartHour; i++) {
					String outputLabel = (i < 10 ? "0" : "") + i + ":00";
					long outputValue = 0;

					if (mappedList.containsKey(outputLabel)) {
						outputValue = mappedList.get(outputLabel);
					}

					lineChartLabels.add(outputLabel);
					dataGraph.add(outputValue);
				}
			} else {
				LocalDate first = statRange.getFrom() != null ? LocalDate.from(statRange.getFrom().toInstant().atZone(UI_ZONE)) : parseKey(list.get(0)).toLocalDate();
				LocalDate last = statRange.getTo() != null ? LocalDate.from(statRange.getTo().toInstant().atZone(UI_ZONE)) : parseKey(list.get(list.size() - 1)).toLocalDate();

				LocalDate cur = first;

				while (!cur.isAfter(last)) {
					String outputLabel = cur.format(OUTPUT_DAILY_FORMAT);
					cur = cur.plusDays(1);

					long outputValue = 0;

					if (mappedList.containsKey(outputLabel)) {
						outputValue = mappedList.get(outputLabel);
					}

					lineChartLabels.add(outputLabel);
					dataGraph.add(outputValue);
				}
			}
		}

		lineChartDataContent.setData(dataGraph);
		lineChartData.add(lineChartDataContent);
		chartData.setLineChartData(lineChartData);
		chartData.setLineChartLabels(lineChartLabels);

		log.debug("Done chart data in " + (System.currentTimeMillis() - start) + " with: " + chartData);

		return chartData;
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
