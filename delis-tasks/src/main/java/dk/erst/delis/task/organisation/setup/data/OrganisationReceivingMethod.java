package dk.erst.delis.task.organisation.setup.data;

public enum OrganisationReceivingMethod {

	FILE_SYSTEM("File system"),

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

	public static OrganisationReceivingMethod getInstance(String value) {
		try {
			return OrganisationReceivingMethod.valueOf(value);
		} catch (Exception e) {
		}
		return null;
	}
}
