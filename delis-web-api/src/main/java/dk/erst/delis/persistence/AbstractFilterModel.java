package dk.erst.delis.persistence;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author funtusthan, created by 13.01.19
 */


@Getter
@Setter
public abstract class AbstractFilterModel {

    private Date start;
    private Date end;
}
