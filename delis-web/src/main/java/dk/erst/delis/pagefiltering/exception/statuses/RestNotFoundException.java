package dk.erst.delis.pagefiltering.exception.statuses;

import dk.erst.delis.pagefiltering.exception.base.RestException;
import dk.erst.delis.pagefiltering.exception.model.FieldErrorModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 09.01.19
 */

@Getter
@Setter
public class RestNotFoundException extends RestException {

	private static final long serialVersionUID = -5205682191735353272L;

	public RestNotFoundException(List<FieldErrorModel> fieldErrors) {
        super(fieldErrors);
    }
}
