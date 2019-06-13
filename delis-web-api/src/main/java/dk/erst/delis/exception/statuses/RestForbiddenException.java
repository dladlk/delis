package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestForbiddenException extends RestException {

    private static final long serialVersionUID = -397618529680592419L;

    public RestForbiddenException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
