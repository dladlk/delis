package dk.erst.delis.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class DateUtilTest {

    @Test
    public void test() {
        Date now = new Date();
        Date oneHourLaterExpected = new Date(now.getTime() + TimeUnit.HOURS.toMillis(1));
        Date oneDayLaterExpected = new Date(now.getTime() + TimeUnit.DAYS.toMillis(1));
        Date oneHourLaterActual = DateUtil.addHour(now, 1);
        assertEquals(oneHourLaterExpected, oneHourLaterActual);

        Date oneDayLaterActual = DateUtil.addDay(now, 1);
        assertEquals(oneDayLaterExpected, oneDayLaterActual);

        long minutes = DateUtil.getMinutesBetween(now, oneHourLaterExpected);
        assertEquals(minutes, 60);

        Date expectedBeginOfDay = DateUtils.truncate(now, Calendar.DATE);
        Date actualBeginOfDay = DateUtil.generateBeginningOfDay(now);
        assertEquals(expectedBeginOfDay, actualBeginOfDay);

    }
}
