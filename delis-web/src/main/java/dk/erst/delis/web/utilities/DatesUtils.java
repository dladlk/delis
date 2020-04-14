package dk.erst.delis.web.utilities;

import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.thymeleaf.util.DateUtils;

@Component("dates")
public class DatesUtils {

	public String datetime(Date date) {
		if (date == null) {
			return "No";
		}
		return DateUtils.format(date, "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	}
}
