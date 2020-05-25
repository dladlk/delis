package dk.erst.delis.validator.service;

import lombok.Data;

@Data
public class ValidateRestResult {
	ValidateResultStatus status;
	int httpStatusCode;
	String body;
}