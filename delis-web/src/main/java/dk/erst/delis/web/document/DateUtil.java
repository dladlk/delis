package dk.erst.delis.web.document;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date getWeekStartDate (Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        c.add(Calendar.DATE, 6);
        Date result = c.getTime();
        return result;
    }

    public static Date getDayStartDate (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getMonthStartDate (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, 0, 0, 0, 0);
        return calendar.getTime();
    }
}
