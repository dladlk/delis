package dk.erst.delis.data.enums.config;

public enum ConfigValueType {

	STORAGE_INPUT_ROOT("config.storageDocumentInput"),

	STORAGE_DOCUMENT_ROOT("config.storageDocumentRoot"),

	STORAGE_VALIDATION_ROOT("config.storageValidationRoot"),
	
	STORAGE_TRANSFORMATION_ROOT("config.storageTransformationRoot"),

	STORAGE_FAILED_ROOT("config.storageFailedRoot"),

	CODE_LISTS_PATH("config.storageCodeListsRoot"),

	ENDPOINT_URL("config.smp.url"),

	ENDPOINT_USER_NAME("config.smp.userName"),

	ENDPOINT_PASSWORD("config.smp.password"),

	ENDPOINT_FORMAT("config.smp.format");

	private final String key;
	ConfigValueType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}

