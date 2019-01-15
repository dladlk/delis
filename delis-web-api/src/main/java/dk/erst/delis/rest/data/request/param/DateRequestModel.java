package dk.erst.delis.rest.data.request.param;

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
public class DateRequestModel {

    Date start;
    Date end;
}
