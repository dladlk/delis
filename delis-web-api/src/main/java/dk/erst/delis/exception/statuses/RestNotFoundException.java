package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestNotFoundException extends RestException {

    private static final long serialVersionUID = -2940148315838628814L;

    public RestNotFoundException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
