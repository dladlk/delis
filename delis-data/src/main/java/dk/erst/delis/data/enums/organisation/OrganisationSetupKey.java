package dk.erst.delis.data.enums.organisation;

public enum OrganisationSetupKey {

	RECEIVING_DATA_FORMAT,

	SMP_INTEGRATION,
	
	SUBSCRIBED_SMP_PROFILES,

	RECEIVING_METHOD,

	RECEIVING_METHOD_SETUP,

	ACCESS_POINT_AS2,

	ACCESS_POINT_AS4,

	GENERATE_RESPONSE_ON_ERROR,

	SEND_UNDELIVERABLE_RESPONSE_TO_ERST,

	RECEIVE_BOTH_BIS3_AND_OIOUBL,

	CHECK_DELIVERED_CONSUMED,

	CHECK_DELIVERED_ALERT_AFTER_MIN,
	
	ON_ERROR_AUTO_SEND_EMAIL_SUPPLIER,

	ON_ERROR_SENDER_EMAIL_ADDRESS,

	ON_ERROR_RECEIVER_EMAIL_ADDRESS,
	;

	public static OrganisationSetupKey getEnum(String value) {
		if (value == null) {
			return null;
		}
		for (OrganisationSetupKey v : values()) {
			if (v.name().equalsIgnoreCase(value)) {
				return v;
			}
		}
		return null;
	}
}
