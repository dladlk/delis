package dk.erst.delis.web.utilities;

import java.util.List;

import org.springframework.stereotype.Component;

import dk.erst.delis.task.organisation.setup.ValidationResultData;

@Component("invalids")
public class InvalidsUtil {

	public String append(ValidationResultData validation, String fieldName) {
		if (validation != null) {
			List<String> errorList = validation.getErrorList(fieldName);
			if (errorList != null) {
				return "is-invalid";
			}
		}
		return "";
	}
}
