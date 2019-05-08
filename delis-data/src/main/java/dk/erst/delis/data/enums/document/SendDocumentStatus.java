package dk.erst.delis.data.enums.document;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.util.BundleUtil;

/*
 * Max length of name 25
 */
public enum SendDocumentStatus implements Named {

	NEW,

	VALIDATE_START, VALID, VALIDATE_ERROR,

	SEND_START, SEND_OK, SEND_ERROR,

	FORWARD_START, FORWARD_OK, FORWARD_SKIPPED, FORWARD_FAILED,
	
	;

	@Override
	public String getName() {
		return BundleUtil.getName(this);
	}
}
