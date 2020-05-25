package dk.erst.delis.validator;

public enum DelisValidatorPesistMode {

	NONE, ALL, FAILED,

	;

	public boolean isNone() {
		return this == NONE;
	}
}
