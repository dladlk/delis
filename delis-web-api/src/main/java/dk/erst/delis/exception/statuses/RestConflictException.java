package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestConflictException extends RestException {

    private static final long serialVersionUID = -258168921783069878L;

    public RestConflictException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
