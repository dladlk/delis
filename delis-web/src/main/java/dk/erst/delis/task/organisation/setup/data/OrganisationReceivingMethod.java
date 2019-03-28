package dk.erst.delis.task.organisation.setup.data;

public enum OrganisationReceivingMethod {

	FILE_SYSTEM("File system"),

	AZURE_STORAGE_ACCOUNT("Azure Storage Account"),

	VFS("VFS")

	;

	private final String title;

	private OrganisationReceivingMethod(String title) {
		this.title = title;
	}
	
	public String getCode() {
		return this.name();
	}

	public String getTitle() {
		return title;
	}
}
