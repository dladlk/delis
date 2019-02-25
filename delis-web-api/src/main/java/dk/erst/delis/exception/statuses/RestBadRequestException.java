package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
public class RestBadRequestException extends RestException {

    public RestBadRequestException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
