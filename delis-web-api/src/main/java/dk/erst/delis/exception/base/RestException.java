package dk.erst.delis.exception.base;

import dk.erst.delis.exception.model.FieldErrorModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestException extends RuntimeException {

    private static final long serialVersionUID = -2766602016898883457L;
    private List<FieldErrorModel> fieldErrors;

    public RestException(List<FieldErrorModel> fieldErrors){
        this.fieldErrors = fieldErrors;
    }
}
