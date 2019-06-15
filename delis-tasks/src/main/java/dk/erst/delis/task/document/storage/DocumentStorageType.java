package dk.erst.delis.task.document.storage;

public enum DocumentStorageType {

	LOADED, FAILED, VALID, SEND;
	
	public String getFolderName() {
		return name();
	}
	
}
