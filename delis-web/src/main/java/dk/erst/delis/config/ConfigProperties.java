package dk.erst.delis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("config")
@Data
public class ConfigProperties {

	private String storageDocumentRoot = "/delis/document";

	private String storageValidationRoot = "/delis/validation";

	private String storageTransformationRoot = "/delis/transformation";
	
	private String storageCodeListsRoot = "../delis-resource/codelists";

}
