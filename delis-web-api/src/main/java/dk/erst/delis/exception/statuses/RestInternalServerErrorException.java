package dk.erst.delis.exception.statuses;

import dk.erst.delis.exception.base.RestException;
import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestInternalServerErrorException extends RestException {

    private static final long serialVersionUID = -2071590497771903572L;

    public RestInternalServerErrorException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
