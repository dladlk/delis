package dk.erst.delis.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.erst.delis.task.document.storage.DocumentStorageType;

@Component
public class ConfigBean {

	private ConfigProperties configProperties;

	@Autowired
	public ConfigBean(ConfigProperties configProperties) {
		this.configProperties = configProperties;
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
		String storageRoot = this.configProperties.getStorageDocumentRoot();
		return Paths.get(storageRoot, storageType.getFolderName());
	}

	public Path getStorageValidationPath() {
		return Paths.get(this.configProperties.getStorageValidationRoot()).toAbsolutePath();
	}

	public Path getStorageTransformationPath() {
		return Paths.get(this.configProperties.getStorageTransformationRoot()).toAbsolutePath();
	}
	
	public Path getStorageCodeListPath() {
		return Paths.get(this.configProperties.getStorageCodeListsRoot()).toAbsolutePath();
	}
}
