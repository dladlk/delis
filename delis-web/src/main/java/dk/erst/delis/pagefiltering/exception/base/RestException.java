package dk.erst.delis.pagefiltering.exception.base;

import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
public class RestException extends RuntimeException {

    private List<FieldErrorModel> fieldErrors;

    public RestException(List<FieldErrorModel> fieldErrors){
        this.fieldErrors = fieldErrors;
    }
}
