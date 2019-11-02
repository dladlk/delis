package dk.erst.delis.rest.data.request.param;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeModel {

    Date start;
    Date end;
}
