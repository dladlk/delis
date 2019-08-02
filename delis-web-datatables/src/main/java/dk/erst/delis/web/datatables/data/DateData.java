package dk.erst.delis.web.datatables.data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DateData {

	private String fieldName;
	private Date startDate;
	private Date endDate;
}
