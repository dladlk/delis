package dk.erst.delis.task.document.storage;

public enum DocumentStorageType {

	LOADED, FAILED, VALID;
	
	public String getFolderName() {
		return name();
	}
	
}
