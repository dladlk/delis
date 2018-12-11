package dk.erst.delis.task.document.storage;

public enum DocumentStorageType {

	INPUT, LOADED, FAILED;
	
	public String getFolderName() {
		return name();
	}
	
}
