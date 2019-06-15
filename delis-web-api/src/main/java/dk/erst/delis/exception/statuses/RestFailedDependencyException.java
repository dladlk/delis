package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestFailedDependencyException extends RestException {

    private static final long serialVersionUID = 457866365261805775L;

    public RestFailedDependencyException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
