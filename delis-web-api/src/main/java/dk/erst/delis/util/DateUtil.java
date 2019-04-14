package dk.erst.delis.util;

import dk.erst.delis.rest.data.request.param.DateRangeModel;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author funtusthan, created by 12.01.19
 */

@UtilityClass
public class DateUtil {

    public final SimpleDateFormat DATE_FORMAT_BY_DAY = new SimpleDateFormat("HH:mm");
    public final SimpleDateFormat DATE_FORMAT_BY_CUSTOM_PERIOD = new SimpleDateFormat("dd.MM");
    public static final String DEFAULT_TIME_ZONE = "Europe/Copenhagen";

    public long getMinutesBetween(Date start, Date end) {
        return ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
    }

    public int getHoursBetween(Date start, Date end) {
        return (int) ChronoUnit.HOURS.between(end.toInstant(), start.toInstant());
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

    public Date generateBeginningOfDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    private Date generateEndOfDay(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public Date convertClientTimeToServerTime(String timeZone, Date date, boolean beginning) {

        // generate server time
        Calendar serverCalendar = Calendar.getInstance();
        if (Objects.nonNull(date)) {
            serverCalendar.setTime(date);
        }
        TimeZone timeZoneServer = serverCalendar.getTimeZone();
        long serverTime = serverCalendar.getTime().getTime();
        Date serverTimeDate = new Date(serverTime);

        // generate client time
        Calendar clientCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        if (Objects.nonNull(date)) {
            clientCalendar.setTime(date);
        }
        TimeZone timeZoneClient = clientCalendar.getTimeZone();

        if (compareTimeZones(timeZoneServer, timeZoneClient)) {
            long clientTime = serverTime + timeZoneClient.getRawOffset();
            Date clientTimeDate = new Date(clientTime);
            int hoursBetween = getHoursBetween(serverTimeDate, clientTimeDate);
            if (beginning) {
                return addHour(generateBeginningOfDay(serverTimeDate), hoursBetween);
            } else {
                return addHour(generateEndOfDay(serverTimeDate), hoursBetween);
            }
        } else {
            if (beginning) {
                return generateBeginningOfDay(serverTimeDate);
            } else {
                return generateEndOfDay(serverTimeDate);
            }
        }
    }

    private boolean compareTimeZones(TimeZone timeZoneServer, TimeZone timeZoneClient) {
        return ObjectUtils.notEqual(timeZoneServer.getID(), timeZoneClient.getID());
    }
}
