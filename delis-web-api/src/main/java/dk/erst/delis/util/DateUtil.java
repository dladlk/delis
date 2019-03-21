package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @author funtusthan, created by 12.01.19
 */

@UtilityClass
public class DateUtil {

    public final SimpleDateFormat DATE_FORMAT_BY_DAY = new SimpleDateFormat("HH:mm");
    public final SimpleDateFormat DATE_FORMAT_BY_CUSTOM_PERIOD = new SimpleDateFormat("MM.dd");

    public long getMinutesBetween(Date start, Date end) {
        return ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
    }

    public DateRangeModel generateDateRangeByLastHour() {
        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR, -1);
        Date start = cal.getTime();
        return new DateRangeModel(start, end);
    }

    public Date addHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    public Date addDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
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

    public Date generateBeginningOfDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public Date generateEndOfDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }
}
