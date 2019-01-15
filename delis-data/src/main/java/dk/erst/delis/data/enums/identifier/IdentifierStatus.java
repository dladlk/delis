package dk.erst.delis.data.enums.identifier;

public enum IdentifierStatus {

	ACTIVE, DELETED;
	
	public static IdentifierStatus getInstanceByName(String code) {
		for (IdentifierStatus e : values()) {
			if (e.name().equalsIgnoreCase(code)) {
				return e;
			}
		}
		return null;
	}

	public boolean isActive() {
		return this == ACTIVE;
	}
	
	public boolean isDeleted() {
		return this == DELETED;
	}
}
