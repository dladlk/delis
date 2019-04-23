package dk.erst.delis.pagefiltering.exception.statuses;

import dk.erst.delis.pagefiltering.exception.base.RestException;
import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author funtusthan, created by 26.03.19
 */

@Getter
@Setter
public class RestInternalServerErrorException extends RestException {

    public RestInternalServerErrorException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
