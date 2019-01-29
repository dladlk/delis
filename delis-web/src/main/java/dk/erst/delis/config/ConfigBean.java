package dk.erst.delis.config;

import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.data.entities.config.ConfigValue;
import dk.erst.delis.data.enums.config.ConfigValueType;
import dk.erst.delis.task.document.storage.DocumentStorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static dk.erst.delis.data.enums.config.ConfigValueType.*;

@Component
public class ConfigBean {

	private ConfigValueDaoRepository configRepository;
	private ConfigProperties configProperties;


	private HashMap <ConfigValueType, String> configValues = new HashMap<>();

	@Autowired
	public ConfigBean(ConfigValueDaoRepository configRepository, ConfigProperties configProperties) {
		this.configRepository = configRepository;
		this.configProperties = configProperties;

		init();
	}

	private void init () {
		ConfigValueType[] valueTypes = ConfigValueType.values();
		for (ConfigValueType valueType : valueTypes) {
			ConfigValue dbValue = configRepository.findByConfigValueType(valueType);
			String value;
			if (dbValue != null) {
				value = dbValue.getValue();
			} else if (System.getenv(valueType.getKey()) != null) {
				value = System.getenv(valueType.getKey());
			} else {
				value = getDefault(valueType);
			}
			configValues.put(valueType, value);
		}
	}

	private String getDefault(ConfigValueType valueType) {
		String defaultValue = null;

		switch (valueType) {
			case STORAGE_DOCUMENT_ROOT:
				defaultValue = this.configProperties.getStorageDocumentRoot();
				break;
			case STORAGE_INPUT_ROOT:
				defaultValue = this.configProperties.getStorageDocumentInput();
				break;
			case STORAGE_VALIDATION_ROOT:
				defaultValue = this.configProperties.getStorageValidationRoot();
				break;
			case STORAGE_TRANSFORMATION_ROOT:
				defaultValue = this.configProperties.getStorageTransformationRoot();
				break;
			case CODE_LISTS_PATH:
				defaultValue = this.configProperties.getStorageCodeListsRoot();
				break;
			case ENDPOINT_URL:
				defaultValue = this.configProperties.getSmp().getUrl();
				break;
			case ENDPOINT_USER_NAME:
				defaultValue = this.configProperties.getSmp().getUserName();
				break;
			case ENDPOINT_PASSWORD:
				defaultValue = this.configProperties.getSmp().getPassword();
				break;
			case ENDPOINT_FORMAT:
				defaultValue = this.configProperties.getSmp().getFormat();
				break;
		}

		return defaultValue;
	}

	public Path getStorageInputPath() {
		String path = configValues.get(STORAGE_INPUT_ROOT);
		return Paths.get(path);
	}

	public Path getStorageLoadedPath() {
		return buildStoragePath(DocumentStorageType.LOADED);
	}

	public Path getStorageFailedPath() {
		return buildStoragePath(DocumentStorageType.FAILED);
	}

	public Path getStorageValidPath() {
		return buildStoragePath(DocumentStorageType.VALID);
	}

	private Path buildStoragePath(DocumentStorageType storageType) {
		String path = configValues.get(STORAGE_DOCUMENT_ROOT);
		return Paths.get(path, storageType.getFolderName());
	}

	public Path getStorageValidationPath() {
		String path = configValues.get(STORAGE_VALIDATION_ROOT);
		return Paths.get(path).toAbsolutePath();
	}

	public Path getStorageTransformationPath() {
		String path = configValues.get(STORAGE_TRANSFORMATION_ROOT);
		return Paths.get(path).toAbsolutePath();
	}

	public Path getStorageCodeListPath() {
		String path = configValues.get(CODE_LISTS_PATH);
		return Paths.get(path).toAbsolutePath();
	}

//	public String getSmpEndpointUrl () {
//		return configValues.get(ENDPOINT_URL);
//	}
//
//	public String getSmpEndpointUserName () {
//		return configValues.get(ENDPOINT_USER_NAME);
//	}
//
//	public String getSmpEndpointPassword () {
//		return configValues.get(ENDPOINT_PASSWORD);
//	}
//
//	public String getSmpEndpointFormat () {
//		return configValues.get(ENDPOINT_FORMAT);
//	}

	public SmpEndpointConfig getSmpEndpointConfig() {
		return new SmpEndpointConfig(configValues.get(ENDPOINT_URL), configValues.get(ENDPOINT_USER_NAME), configValues.get(ENDPOINT_PASSWORD), configValues.get(ENDPOINT_FORMAT));
	}
}
