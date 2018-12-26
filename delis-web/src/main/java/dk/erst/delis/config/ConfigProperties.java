package dk.erst.delis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("config")
@Data
public class ConfigProperties {

	private static String DEFAULT_ROOT = "../delis-resources/";

	private String storageDocumentRoot = "/delis/document";

	private String storageValidationRoot = DEFAULT_ROOT + "validation";

	private String storageTransformationRoot = DEFAULT_ROOT + "transformation";

	private String storageCodeListsRoot = DEFAULT_ROOT + "codelists";

}
