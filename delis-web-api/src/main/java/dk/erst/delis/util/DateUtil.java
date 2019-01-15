package dk.erst.delis.util;

import lombok.experimental.UtilityClass;

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
}
