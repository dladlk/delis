package dk.erst.delis.pagefiltering.exception.statuses;

import dk.erst.delis.pagefiltering.exception.base.RestException;
import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author funtusthan, created by 12.01.19
 */

@Getter
@Setter
public class RestUnauthorizedException extends RestException {

    public RestUnauthorizedException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
