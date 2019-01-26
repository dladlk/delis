package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author funtusthan, created by 12.01.19
 */

@UtilityClass
public class DateUtil {

    public long rangeHoursDate(Date date) {
        Calendar between = Calendar.getInstance();
        between.setTime(date);
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        long difference = today.getTimeInMillis() - between.getTimeInMillis();
        return (int) difference / 1000 / 60;
    }

    public DateRangeModel generateDateRangeByLastHour() {
        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR, -1);
        Date start = cal.getTime();
        return new DateRangeModel(start, end);
    }

    public DateRangeModel generateDateRangeByFromAndToLastHour(int timeType, int time, int interval) {
        Date start = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(timeType, -time);
        start = cal.getTime();

        cal.setTime(start);
        cal.add(timeType, interval);
        Date end = cal.getTime();
        return new DateRangeModel(start, end);
    }

    public Date generateBeginningOfDay() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        return Date.from(zonedDateTime.toLocalDate().atStartOfDay(zoneId).toInstant());
    }
}
