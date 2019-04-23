package dk.erst.delis.pagefiltering.exception.model;

import dk.erst.delis.pagefiltering.exception.base.RestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseModel {

    private List<FieldErrorModel> fieldErrors;

    public ErrorResponseModel(RestException e) {
        this.fieldErrors = e.getFieldErrors();
    }
}
