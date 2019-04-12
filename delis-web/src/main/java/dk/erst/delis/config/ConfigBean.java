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


	private HashMap <ConfigValueType, String> configValues = new HashMap<>();

	@Autowired
	public ConfigBean(ConfigValueDaoRepository configRepository) {
		this.configRepository = configRepository;

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
				value = valueType.getDefaultValue();
			}
			configValues.put(valueType, value);
		}
	}

	public void refresh () {
		init();
	}

	public Path getStorageInputPath() {
		String path = configValues.get(STORAGE_INPUT_ROOT);
		return Paths.get(path);
	}

	public Path getIdentifierInputPath() {
		String path = configValues.get(IDENTIFIER_INPUT_ROOT);
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

	public Path getDocumentRootPath() {
		String path = configValues.get(STORAGE_DOCUMENT_ROOT);
		return Paths.get(path).toAbsolutePath();
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

	/**
	 * @return by default - true
	 */
	public boolean getXsltCacheEnabled() {
		String stringValue = configValues.get(XSLT_CACHE_ENABLED);
		if (stringValue != null) {
			return Boolean.parseBoolean(stringValue);
		}
		return true;
	}

	public SmpEndpointConfig getSmpEndpointConfig() {
		return new SmpEndpointConfig(configValues.get(ENDPOINT_URL), configValues.get(ENDPOINT_USER_NAME), configValues.get(ENDPOINT_PASSWORD), configValues.get(ENDPOINT_FORMAT));
	}
}
