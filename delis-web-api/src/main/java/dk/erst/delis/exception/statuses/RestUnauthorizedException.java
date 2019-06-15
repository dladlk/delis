package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestUnauthorizedException extends RestException {

    private static final long serialVersionUID = 2579970359209648014L;

    public RestUnauthorizedException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
