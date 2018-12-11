package dk.erst.delis.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.erst.delis.dao.ConfigValueRepository;
import dk.erst.delis.data.ConfigValue;
import dk.erst.delis.data.ConfigValueType;
import dk.erst.delis.task.document.storage.DocumentStorageType;

@Component
public class ConfigBean {

	@Autowired
	private ConfigValueRepository configValueRepository;
	
	public String getConfigValue(ConfigValueType configValueType) {
		ConfigValue configValue = configValueRepository.findByConfigValueType(configValueType);
		if (configValue != null) {
			return configValue.getValue();
		}
		return null;
	}
	
	public Path getStorageInputPath() {
		return buildStoragePath(DocumentStorageType.INPUT);
	}
	
	public Path getStorageLoadedPath() {
		return buildStoragePath(DocumentStorageType.LOADED);
	}

	public Path getStorageFailedPath() {
		return buildStoragePath(DocumentStorageType.FAILED);
	}
	
	private Path buildStoragePath(DocumentStorageType storageType) {
		String storageRoot = this.getConfigValue(ConfigValueType.STORAGE_DOCUMENT_ROOT);
		return Paths.get(storageRoot, storageType.getFolderName());
	}
}
