package dk.erst.delis.web.datatables.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class DateData {

    private String fieldName;
    private Date startDate;
    private Date endDate;
}
