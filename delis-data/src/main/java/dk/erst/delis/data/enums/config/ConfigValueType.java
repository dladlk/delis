package dk.erst.delis.data.enums.config;

public enum ConfigValueType {

	IDENTIFIER_INPUT_ROOT("config.storageIdentifiersInput", "../delis/identifier/input"),

	STORAGE_INPUT_ROOT("config.storageDocumentInput","../delis/input"),

	STORAGE_DOCUMENT_ROOT("config.storageDocumentRoot","../delis/document"),

	STORAGE_VALIDATION_ROOT("config.storageValidationRoot","../delis-resources/validation"),
	
	STORAGE_TRANSFORMATION_ROOT("config.storageTransformationRoot","../delis-resources/transformation"),

	CODE_LISTS_PATH("config.storageCodeListsRoot","../delis-resources/codelists"),

	ENDPOINT_URL("config.smp.url","http://localhost:8090/smp-4.1.0"),

	ENDPOINT_USER_NAME("config.smp.userName", "smp_admin"),

	ENDPOINT_PASSWORD("config.smp.password", "changeit"),

	XSLT_CACHE_ENABLED("config.xslt.cache.enabled", "true"),

	ENDPOINT_FORMAT("config.smp.format", "OASIS");

	private final String key;
	private final String defaultValue;

	ConfigValueType(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}

