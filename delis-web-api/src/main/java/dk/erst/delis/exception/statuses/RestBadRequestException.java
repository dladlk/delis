package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestBadRequestException extends RestException {

    private static final long serialVersionUID = 8381961857624047745L;

    public RestBadRequestException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
