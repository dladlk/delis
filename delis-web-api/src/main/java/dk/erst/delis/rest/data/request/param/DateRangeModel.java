package dk.erst.delis.rest.data.request.param;

import lombok.*;

import java.util.Date;

/**
 * @author funtusthan, created by 14.01.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeModel {

    Date start;
    Date end;
}
