package dk.erst.delis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("config")
@Data
public class ConfigProperties {

	private static String DEFAULT_ROOT = "../delis-resources/";

	private String storageDocumentInput = "/delis/input";

	private String storageDocumentRoot = "/delis/document";

	private String storageValidationRoot = DEFAULT_ROOT + "validation";

	private String storageTransformationRoot = DEFAULT_ROOT + "transformation";

	private String storageCodeListsRoot = DEFAULT_ROOT + "codelists";

	private SmpEndpointConfig smp = new SmpEndpointConfig();

}
