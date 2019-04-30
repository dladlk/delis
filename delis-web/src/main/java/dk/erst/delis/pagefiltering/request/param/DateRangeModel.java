package dk.erst.delis.pagefiltering.request.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
